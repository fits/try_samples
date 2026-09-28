use candle_core::{DType, Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::generation::LogitsProcessor;
use candle_transformers::models::mamba::{Config, Model, State};
use tokenizers::Tokenizer;

use std::env;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error + Send + Sync>>;

fn main() -> Result<()> {
    let device = Device::new_metal(0)?;
    let dtype = DType::F32;

    let temperature = Some(1.0);
    let top_p = Some(0.3);

    let mut args = env::args().skip(1);

    let prompt = args.next().ok_or("prompt")?;

    let max_sample_len: usize = args.next().and_then(|x| x.parse().ok()).unwrap_or(100);

    let seed = args.next().and_then(|x| x.parse().ok()).unwrap_or(123);

    let tokenizer = Tokenizer::from_file("model/tokenizer.json")?;

    let config: Config = serde_json::from_str(&std::fs::read_to_string("model/config.json")?)?;

    let vb = VarBuilder::from_pth("model/pytorch_model.bin", dtype, &device)?;

    let model = Model::new(&config, vb.pp("backbone"))?;

    let mut logits_proc = LogitsProcessor::new(seed, temperature, top_p);

    let tokens = tokenizer.encode(prompt, true)?.get_ids().to_vec();

    let eos_token = tokenizer
        .token_to_id("<|endoftext|>")
        .ok_or("not found endoftext token")?;

    let mut state = State::new(1, &config, dtype, &device)?;

    let mut next_logits = None;

    for t in tokens {
        let input = Tensor::new(&[t], &device)?;
        let logits = model.forward(&input, &mut state)?;

        next_logits = Some(logits);
    }

    for _index in 0..max_sample_len {
        let logits = next_logits
            .ok_or("no token result")?
            .squeeze(0)?
            .to_dtype(DType::F32)?;

        let token_id = logits_proc.sample(&logits)?;

        if token_id == eos_token {
            break;
        }

        let token = tokenizer.decode(&[token_id], true)?;
        print!("{token}");

        let input = Tensor::new(&[token_id], &device)?;

        next_logits = Some(model.forward(&input, &mut state)?);
    }

    Ok(())
}
