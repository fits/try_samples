use std::env;
use std::fs;
use std::io;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let path = env::args().skip(1).next().ok_or("err")?;

    let content = fs::read_to_string(path)?;

    let parser = pulldown_cmark::Parser::new(&content);

    pulldown_cmark::html::write_html(io::stdout(), parser)?;

    Ok(())
}
