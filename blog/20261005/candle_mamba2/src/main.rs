use candle_core::safetensors::Load;
use candle_core::{DType, Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::generation::LogitsProcessor;
use candle_transformers::models::mamba2::{Config, Model, State};
use candle_transformers::utils;
use tokenizers::Tokenizer;

use std::env;
use std::fs::File;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error + Send + Sync>>;

const CONFIG_FILE: &str = "model/config.json";
const TOKENIZER_FILE: &str = "model/tokenizer.json";
const MODEL_FILE: &str = "model/model.safetensors";

const KEY_HS: &str = "hs-";
const KEY_CONV_STATES: &str = "conv-states-";
const KEY_POS: &str = "pos";

const TEMPERATURE: Option<f64> = Some(0.7);
const TOP_P: Option<f64> = Some(0.7);
const REPEAT_PENALTY: f32 = 1.1;
const REPEAT_LAST_N: usize = 32;
const REPEAT_PENALTY_MIN_TOKEN_SIZE: Option<usize> = Some(5);
const MAX_SAMPLE_LEN: usize = 100;

fn main() -> Result<()> {
    let device = Device::new_metal(0)?;
    let dtype = DType::F32;

    let mut args = env::args().skip(1);

    let prompt = args.next().ok_or("prompt")?;
    let seed = args.next().and_then(|x| x.parse().ok()).unwrap_or(12345);

    let output_state_file = args.next();
    let input_state_file = args.next();

    let tokenizer = Tokenizer::from_file(TOKENIZER_FILE)?;
    let config: Config = serde_json::from_str(&std::fs::read_to_string(CONFIG_FILE)?)?;

    let vb = unsafe { VarBuilder::from_mmaped_safetensors(&[MODEL_FILE], dtype, &device)? };

    let model = Model::new(&config, vb.pp("backbone"))?;

    let mut logits_proc = LogitsProcessor::new(seed, TEMPERATURE, TOP_P);

    print!("{prompt}");

    let tokens = tokenizer.encode(prompt, true)?.get_ids().to_vec();

    let eos_token = tokenizer
        .token_to_id("<|endoftext|>")
        .ok_or("not found endoftext token")?;

    let mut state = State::new(1, &config, dtype, &device)?;

    if let Some(input_file) = input_state_file {
        load_state(&mut state, &input_file, &device)?;
    }

    let mut next_logits = None;

    for t in tokens {
        let input = Tensor::new(&[t], &device)?;
        let logits = model.forward(&input, &mut state)?;

        next_logits = Some(logits);
    }

    let mut output_token_ids = vec![];

    for _ in 0..MAX_SAMPLE_LEN {
        let logits = next_logits
            .ok_or("no token result")?
            .squeeze(0)?
            .to_dtype(DType::F32)?;

        let logits = apply_repeat_penalty(
            &logits,
            REPEAT_LAST_N,
            REPEAT_PENALTY,
            &output_token_ids,
            REPEAT_PENALTY_MIN_TOKEN_SIZE,
        )?;

        let token_id = logits_proc.sample(&logits)?;

        if token_id == eos_token {
            break;
        }

        output_token_ids.push(token_id);

        let token = tokenizer.decode(&[token_id], true)?;
        print!("{token}");

        let input = Tensor::new(&[token_id], &device)?;

        next_logits = Some(model.forward(&input, &mut state)?);
    }

    if let Some(output_file) = output_state_file {
        save_state(&state, &output_file)?;
    }

    Ok(())
}

fn save_state(state: &State, file: &str) -> Result<()> {
    let device = state.hs.first().map(|x| x.device()).ok_or("no tensor")?;

    let mut state_data = vec![];

    for (i, t) in state.hs.iter().enumerate() {
        state_data.push((format!("{KEY_HS}{i:03}"), t));
    }

    for (i, t) in state.conv_states.iter().enumerate() {
        state_data.push((format!("{KEY_CONV_STATES}{i:03}"), t));
    }

    let pos = Tensor::from_slice(&[state.pos as u32], 1, device)?;
    state_data.push((KEY_POS.into(), &pos));

    safetensors::tensor::serialize_to_file(state_data, None, file.as_ref())?;

    Ok(())
}

fn load_state(state: &mut State, file: &str, device: &Device) -> Result<()> {
    let f = File::open(file)?;
    let buffer = unsafe { memmap2::MmapOptions::new().map(&f)? };

    let ts = safetensors::SafeTensors::deserialize(&buffer)?;

    let mut keys = ts.names();
    keys.sort();

    let mut hs = vec![];
    let mut conv_states = vec![];
    let mut pos = 0;

    for key in keys {
        let t = ts.tensor(key)?.load(device)?;

        if key.starts_with(KEY_HS) {
            hs.push(t);
        } else if key.starts_with(KEY_CONV_STATES) {
            conv_states.push(t);
        } else if key.eq(KEY_POS) {
            pos = t.get(0)?.to_scalar::<u32>()? as usize;
        }
    }

    state.hs = hs;
    state.conv_states = conv_states;
    state.pos = pos;

    Ok(())
}

fn apply_repeat_penalty(
    logits: &Tensor,
    repeat_last_n: usize,
    repeat_penalty: f32,
    token_ids: &Vec<u32>,
    min_token_size: Option<usize>,
) -> Result<Tensor> {
    if token_ids.len() >= min_token_size.unwrap_or_default().max(1) {
        let idx = token_ids.len().saturating_sub(repeat_last_n);

        utils::apply_repeat_penalty(&logits, repeat_penalty, &token_ids[idx..])
            .map_err(|e| e.into())
    } else {
        Ok(logits.to_owned())
    }
}
