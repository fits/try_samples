use candle_core::{DType, Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::generation::LogitsProcessor;
use candle_transformers::models::phi::{Config, Model};
use tokenizers::{Result, Tokenizer};

fn main() -> Result<()> {
    let seed = 1234;
    let max_len: usize = 1000;

    let prompt = std::env::args().skip(1).next().ok_or("prompt arg")?;

    let device = Device::Cpu;

    let tokenizer = Tokenizer::from_file("tokenizer.json")?;

    let config_str = std::fs::read_to_string("config.json")?;
    let config: Config = serde_json::from_str(&config_str)?;

    let vb = unsafe {
        VarBuilder::from_mmaped_safetensors(&["model.safetensors"], DType::F32, &device)?
    };

    let mut phi = Model::new(&config, vb)?;

    let mut logits_proc = LogitsProcessor::new(seed, None, None);

    let tokens = tokenizer.encode(prompt, true)?;

    if tokens.is_empty() {
        return Err("empty token".into());
    }

    let eos_token = tokenizer
        .token_to_id("<|endoftext|>")
        .ok_or("not found endoftext token")?;

    let mut tokens = tokens.get_ids().to_vec();
    let mut output_tokens = vec![];

    for _index in 0..max_len {
        let input = Tensor::new(&tokens[0..], &device)?.unsqueeze(0)?;

        let logits = phi.forward(&input)?;

        let logits = logits.squeeze(0)?.to_dtype(DType::F32)?;

        let next_token = logits_proc.sample(&logits)?;

        output_tokens.push(next_token);

        if next_token == eos_token {
            break;
        }

        tokens = vec![next_token];
    }

    let output = tokenizer.decode(&output_tokens, true)?;

    print!("{output}");

    Ok(())
}
