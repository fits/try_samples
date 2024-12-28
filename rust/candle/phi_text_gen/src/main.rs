use candle_core::{DType, Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::generation::LogitsProcessor;
use candle_transformers::models::phi::{Config, Model};
use std::env;
use tokenizers::{Result, Tokenizer};

fn main() -> Result<()> {
    let max_sample_len: usize = 1000;
    let buffer_len: usize = 200;
    let temperature = Some(1.0);
    let top_p = Some(0.3);

    let prompt = env::args().nth(1).ok_or("prompt")?;

    let seed = env::args()
        .nth(2)
        .and_then(|x| x.parse().ok())
        .unwrap_or(299792458);

    let device = Device::Cpu;

    let tokenizer = Tokenizer::from_file("tokenizer.json")?;

    let config_str = std::fs::read_to_string("config.json")?;
    let config: Config = serde_json::from_str(&config_str)?;

    let vb = unsafe {
        VarBuilder::from_mmaped_safetensors(&["model.safetensors"], DType::F32, &device)?
    };

    let mut phi = Model::new(&config, vb)?;

    let mut logits_proc = LogitsProcessor::new(seed, temperature, top_p);

    let tokens = tokenizer.encode(prompt, true)?;

    if tokens.is_empty() {
        return Err("empty token".into());
    }

    let eos_token = tokenizer
        .token_to_id("<|endoftext|>")
        .ok_or("not found endoftext token")?;

    let mut tokens = tokens.get_ids().to_vec();
    let mut output_tokens = vec![];

    for _index in 0..max_sample_len {
        let input = Tensor::new(&*tokens, &device)?.unsqueeze(0)?;

        let logits = phi.forward(&input)?;

        let logits = logits.squeeze(0)?.to_dtype(DType::F32)?;

        let next_token = logits_proc.sample(&logits)?;

        output_tokens.push(next_token);

        if next_token == eos_token {
            break;
        }

        if output_tokens.len() >= buffer_len {
            print_tokens(&tokenizer, &output_tokens);
            output_tokens.clear();
        }

        tokens = vec![next_token];
    }

    print_tokens(&tokenizer, &output_tokens);

    Ok(())
}

fn print_tokens(tokenizer: &Tokenizer, tokens: &Vec<u32>) {
    if !tokens.is_empty() {
        if let Ok(t) = tokenizer.decode(tokens, true) {
            print!("{t}")
        }
    }
}
