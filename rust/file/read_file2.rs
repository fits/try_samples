
use std::fs::File;
use std::io::Read;
use std::io::Result;

fn main() {
	let s = read_file("sample.txt");

	println!("{}", s.unwrap());
}

fn read_file(file_name: &'static str) -> Result<String> {
	let mut f = try!(File::open(file_name));

	let mut s = String::new();

	try!(f.read_to_string(&mut s));

	return Ok(s);
}
