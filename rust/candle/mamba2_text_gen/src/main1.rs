use candle_core::safetensors::Load;
use candle_core::{DType, Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::generation::LogitsProcessor;
use candle_transformers::models::mamba2::{Config, Model, State};
use tokenizers::Tokenizer;

use std::env;
use std::fs::File;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error + Send + Sync>>;

const KEY_HS: &str = "hs-";
const KEY_CONV_STATES: &str = "conv-states-";
const KEY_POS: &str = "pos";

fn main() -> Result<()> {
    let state_file = "state1.safetensors";

    let device = Device::new_metal(0)?;
    let dtype = DType::F32;

    let temperature = Some(0.7);
    let top_p = Some(0.9);

    let repeat_penalty = 1.1;
    let repeat_last_n = 32;
    let repeat_ngram_size = 4;

    let mut args = env::args().skip(1);

    let prompt = args.next().ok_or("prompt")?;

    let max_sample_len: usize = args.next().and_then(|x| x.parse().ok()).unwrap_or(100);

    let seed = args
        .next()
        .and_then(|x| x.parse().ok())
        .unwrap_or(123456789);

    let tokenizer = Tokenizer::from_file("model/tokenizer.json")?;

    let config: Config = serde_json::from_str(&std::fs::read_to_string("model/config.json")?)?;

    let vb = unsafe {
        VarBuilder::from_mmaped_safetensors(&["model/model.safetensors"], dtype, &device)?
    };

    let model = Model::new(&config, vb.pp("backbone"))?;

    let mut logits_proc = LogitsProcessor::new(seed, temperature, top_p);

    print!("{prompt}");

    let tokens = tokenizer.encode(prompt, true)?.get_ids().to_vec();

    let eos_token = tokenizer
        .token_to_id("<|endoftext|>")
        .ok_or("not found endoftext token")?;

    let mut state = State::new(1, &config, dtype, &device)?;

    load_state(&mut state, state_file, &device)?;

    let mut next_logits = None;

    for t in tokens {
        let input = Tensor::new(&[t], &device)?;
        let logits = model.forward(&input, &mut state)?;

        next_logits = Some(logits);
    }

    let mut output_token_ids = vec![];

    for _index in 0..max_sample_len {
        let mut logits = next_logits
            .ok_or("no token result")?
            .squeeze(0)?
            .to_dtype(DType::F32)?;

        let st = output_token_ids.len().saturating_sub(repeat_last_n);

        logits = candle_transformers::utils::apply_repeat_penalty(
            &logits,
            repeat_penalty,
            &output_token_ids[st..],
        )?;

        logits = apply_ngram_repeat_penalty(&logits, repeat_ngram_size, &output_token_ids)?;

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

    save_state(&state, state_file)?;

    Ok(())
}

fn save_state(state: &State, file: &str) -> Result<()> {
    let device = state.hs.first().map(|x| x.device()).ok_or("no tensor")?;

    let hs_data = state
        .hs
        .iter()
        .enumerate()
        .map(|(i, t)| (format!("{KEY_HS}{i:03}"), t))
        .collect::<Vec<_>>();

    let conv_data = state
        .conv_states
        .iter()
        .enumerate()
        .map(|(i, t)| (format!("{KEY_CONV_STATES}{i:03}"), t))
        .collect();

    let mut state_data = [hs_data, conv_data].concat();

    let pos = Tensor::from_slice(&[state.pos as u32], 1, device)?;
    state_data.push((KEY_POS.into(), &pos));

    safetensors::tensor::serialize_to_file(state_data, None, file.as_ref())?;

    Ok(())
}

fn load_state(state: &mut State, file: &str, device: &Device) -> Result<()> {
    if let Ok(f) = File::open(file) {
        let buffer = unsafe { memmap2::MmapOptions::new().map(&f)? };

        let tensors = safetensors::SafeTensors::deserialize(&buffer)?;

        let mut ks = tensors.names();
        ks.sort();

        let mut hs = vec![];
        let mut conv_states = vec![];
        let mut pos = 0;

        for k in ks {
            let t = tensors.tensor(k)?.load(device)?;

            if k.starts_with(KEY_HS) {
                hs.push(t);
            } else if k.starts_with(KEY_CONV_STATES) {
                conv_states.push(t);
            } else if k.eq(KEY_POS) {
                pos = t.get(0)?.to_scalar::<u32>()? as usize;
            }
        }

        state.hs = hs;
        state.conv_states = conv_states;
        state.pos = pos;
    }

    Ok(())
}

fn apply_ngram_repeat_penalty(
    logits: &Tensor,
    ngram_size: usize,
    token_ids: &Vec<u32>,
) -> Result<Tensor> {
    if ngram_size >= 2 && token_ids.len() > ngram_size {
        let idx = token_ids.len() - (ngram_size - 1);

        if let Some(prefix) = token_ids.get(idx..) {
            let mut ng_ts = vec![];

            for ts in token_ids.windows(ngram_size) {
                if ts.starts_with(prefix) {
                    ng_ts.push(ts.last().unwrap());
                }
            }

            if !ng_ts.is_empty() {
                let device = logits.device();
                let mut logits = logits.to_dtype(DType::F32)?.to_vec1::<f32>()?;

                for t in ng_ts {
                    if let Some(x) = logits.get_mut(*t as usize) {
                        *x = f32::NEG_INFINITY;
                    }
                }

                let shape = logits.len();
                let res = Tensor::from_vec(logits, shape, device)?;

                return Ok(res);
            }
        }
    }

    Ok(logits.to_owned())
}
