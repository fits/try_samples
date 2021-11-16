use lindera::tokenizer::Tokenizer;
use lindera_core::LinderaResult;

use std::env;

fn main() -> LinderaResult<()> {
    let text = env::args().nth(1).unwrap_or("".to_string());

    let mut tokenizer = Tokenizer::new()?;
    let tokens = tokenizer.tokenize(&text)?;

    for token in tokens {
        println!("text: {}, detail: {:?}", token.text, token.detail);
    }

    Ok(())
}
