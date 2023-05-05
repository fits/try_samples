use std::env;
use std::fs::File;
use std::io::Write;

fn main() -> std::io::Result<()> {
    let file_name = env::args().nth(1).unwrap_or_default();

    let mut file = File::create(file_name)?;

    file.write_all(b"sample data")?;

    Ok(())
}
