use candle_core::{DType, Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::generation::LogitsProcessor;
use candle_transformers::models::gemma2::{Config, Model};
use candle_transformers::utils::apply_repeat_penalty;
use std::env;
use tokenizers::{Result, Tokenizer};

fn main() -> Result<()> {
    let tokenizer_file = "model/tokenizer.json";
    let config_file = "model/config.json";
    let weight_files = [
        "model/model-00001-of-00003.safetensors",
        "model/model-00002-of-00003.safetensors",
        "model/model-00003-of-00003.safetensors",
    ];

    let max_sample_len: usize = 1000;
    let buffer_len: usize = 256;
    let temperature = Some(0.4);
    let top_p = Some(0.2);
    let repeat_penalty = 1.1;
    let repeat_last_n = 128;

    let prompt = env::args().nth(1).ok_or("prompt")?;

    let seed = env::args()
        .nth(2)
        .and_then(|x| x.parse().ok())
        .unwrap_or(12345);

    let device = Device::Cpu;

    let tokenizer = Tokenizer::from_file(tokenizer_file)?;

    let config_str = std::fs::read_to_string(config_file)?;
    let config: Config = serde_json::from_str(&config_str)?;

    let vb = unsafe { VarBuilder::from_mmaped_safetensors(&weight_files, DType::F32, &device)? };

    let mut gemma = Model::new(false, &config, vb)?;

    let mut logits_proc = LogitsProcessor::new(seed, temperature, top_p);

    let tokens = tokenizer.encode(prompt, true)?;

    if tokens.is_empty() {
        return Err("empty token".into());
    }

    let eos_token = tokenizer
        .token_to_id("<eos>")
        .ok_or("not found eos token")?;

    let mut tokens = tokens.get_ids().to_vec();
    let mut output_tokens = vec![];
    let mut ctx_size = tokens.len();

    for _index in 0..max_sample_len {
        let offset = tokens.len().saturating_sub(ctx_size);
        let input = Tensor::new(&tokens[offset..], &device)?.unsqueeze(0)?;

        let logits = gemma.forward(&input, offset)?;

        let logits = logits.squeeze(0)?.squeeze(0)?.to_dtype(DType::F32)?;

        let logits = apply_repeat_penalty(
            &logits,
            repeat_penalty,
            &tokens[tokens.len().saturating_sub(repeat_last_n)..],
        )?;

        let next_token = logits_proc.sample(&logits)?;

        if next_token == eos_token {
            break;
        }

        output_tokens.push(next_token);

        if output_tokens.len() >= buffer_len {
            print_tokens(&tokenizer, &output_tokens);
            output_tokens.clear();
        }

        ctx_size = 1;
        tokens.push(next_token);
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
