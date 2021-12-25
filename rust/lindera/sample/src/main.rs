use lindera::tokenizer::Tokenizer;
use lindera_core::LinderaResult;

use std::env;

fn main() -> LinderaResult<()> {
    let text = env::args().nth(1).unwrap_or("".to_string());

    let mut tokenizer = Tokenizer::new()?;
    let ts = tokenizer.tokenize(&text)?;

    for t in ts {
        let pos = t.detail.get(0..4).unwrap_or(&[]);
        println!("text={}, partOfSpeech={:?}", t.text, pos);
    }

    Ok(())
}
