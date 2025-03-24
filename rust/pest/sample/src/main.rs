extern crate alloc;

use pest::Parser;
use pest_derive::Parser;

#[derive(Parser)]
#[grammar = "sample.pest"]
struct TokenParser;

fn main() -> Result<(), pest::error::Error<Rule>> {
    let p1 = TokenParser::parse(Rule::token, "test")?;
    println!("{:?}", p1);

    let p2 = TokenParser::parse(Rule::token, "a")?;
    println!("{:?}", p2);

    Ok(())
}
