
use lindera_tantivy::tokenizer::LinderaTokenizer;
use tantivy::tokenizer::Tokenizer;

use std::env;

fn main() {
    let text = env::args().skip(1).next().unwrap_or("".to_string());
    let tokenizer = LinderaTokenizer::new().unwrap();

    let mut stream = tokenizer.token_stream(&text);

    while stream.advance() {
        println!("{:?}", stream.token());
    }
}
