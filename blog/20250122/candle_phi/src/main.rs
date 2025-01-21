use candle_core::{DType, Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::generation::LogitsProcessor;
use candle_transformers::models::phi::{Config, Model};
use std::env;
use tokenizers::{Result, Tokenizer};

fn main() -> Result<()> {
    let config_file = "model/config.json";
    let tokenizer_file = "model/tokenizer.json";
    let model_file = "model/model.safetensors";

    let max_len: usize = 1000;
    let temperature = Some(0.8);
    let top_p = Some(0.5);

    let prompt = env::args().nth(1).ok_or("prompt")?;

    let seed = env::args()
        .nth(2)
        .and_then(|x| x.parse().ok())
        .unwrap_or(1);

    let device = Device::Cpu;

    let config_str = std::fs::read_to_string(config_file)?;
    let config: Config = serde_json::from_str(&config_str)?;

    let vb = unsafe { VarBuilder::from_mmaped_safetensors(&[model_file], DType::F32, &device)? };
    let mut model = Model::new(&config, vb)?;

    let mut logits_proc = LogitsProcessor::new(seed, temperature, top_p);

    let tokenizer = Tokenizer::from_file(tokenizer_file)?;

    let tokens = tokenizer.encode(prompt, true)?;

    if tokens.is_empty() {
        return Err("empty token".into());
    }

    let eos_token = tokenizer
        .token_to_id("<|endoftext|>")
        .ok_or("not found eos token")?;

    let mut tokens = tokens.get_ids().to_vec();
    let mut output_tokens = vec![];

    for _ in 0..max_len {
        let input = Tensor::new(tokens.as_slice(), &device)?.unsqueeze(0)?;

        let logits = model.forward(&input)?;

        let logits = logits.squeeze(0)?.to_dtype(DType::F32)?;

        let next_token = logits_proc.sample(&logits)?;

        if next_token == eos_token {
            break;
        }

        output_tokens.push(next_token);
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
