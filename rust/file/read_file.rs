
use std::fs::File;
use std::io::Read;

fn main() {
	let mut f = File::open("sample.txt").unwrap();

	let mut s = String::new();

	f.read_to_string(&mut s).unwrap();

	println!("{}", s);
}
