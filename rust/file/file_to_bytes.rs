
use std::io;
use std::io::prelude::*;
use std::fs::File;

fn to_bytes(file: &str) -> io::Result<Vec<u8>> {
    let mut f = File::open(file)?;
    let mut buffer = Vec::new();

    f.read_to_end(&mut buffer)?;

    Ok(buffer)
}

fn main() -> io::Result<()> {
    let bytes1 = include_bytes!("sample.txt");
    println!("{:?}", bytes1);
    assert_eq!(bytes1, b"a1\r\nb2\r\nc3\r\n");

    let bytes2 = to_bytes("sample.txt")?;
    println!("{:?}", bytes2);
    assert_eq!(bytes2, b"a1\r\nb2\r\nc3\r\n");

    Ok(())
}