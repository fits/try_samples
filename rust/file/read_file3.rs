
use std::env;
use std::fs::File;
use std::io::{Read, Result};

fn main() -> Result<()> {
    let path = env::args().nth(1).unwrap_or_default();

    let mut file = File::open(path)?;
    let mut content = String::new();

    file.read_to_string(&mut content)?;

    print!("{}", content);

    Ok(())
}
