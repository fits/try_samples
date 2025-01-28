use std::env;

use tokenizers::models::bpe::BPE;
use tokenizers::{AddedToken, Result, Tokenizer};

fn main() -> Result<()> {
    let vocab_file = env::args().nth(1).ok_or("vocab.json path")?;
    let merges_file = env::args().nth(2).ok_or("merges.txt path")?;

    let bpe = BPE::from_file(&vocab_file, &merges_file).build()?;

    let mut tokenizer = Tokenizer::new(bpe);

    tokenizer.add_special_tokens(&[
        AddedToken::from("<|startoftext|>", true).normalized(true),
        AddedToken::from("<|endoftext|>", true),
    ]);

    tokenizer.save("tokenizer.json", true)
}
