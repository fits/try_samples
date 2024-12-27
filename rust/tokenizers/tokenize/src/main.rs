use tokenizers::{Tokenizer, Result};

fn main() -> Result<()> {
    let tokenizer = Tokenizer::from_file("tokenizer.json")?;

    let input = "a domain model in Domain-Driven Design";

    let tokens = tokenizer.encode(input, false)?;

    println!("ids={:?}, tokens={:?}", tokens.get_ids(), tokens.get_tokens());
    
    let eos = tokenizer.token_to_id("<|endoftext|>");

    println!("end of text token_id={eos:?}");

    Ok(())
}
