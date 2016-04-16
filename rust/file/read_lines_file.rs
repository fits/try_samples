
use std::env;

use std::fs::File;
use std::io::{BufRead, BufReader};

fn main() {
	let a = env::args().skip(1).next()
		.and_then(|s| File::open(s).ok())
		.map(|f| BufReader::new(f).lines());

	for lines in a {
		for line in lines {
			println!("{}", line.unwrap());
			// println!("{}", line.unwrap_or(String::from("")));
		}
	}
}