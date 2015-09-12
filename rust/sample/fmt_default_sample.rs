
use std::fmt;

fn main() {
	let d = Data { name: "data1", value: 10 };

	println!("{}", d);   // Display
	println!("{:?}", d); // Debug

	let d2 = Data2 { name: "data1", value: 10 };

	println!("debug : {:?}", d2); // Debug
	println!("debug : {:#?}", d2); // Debug
}

struct Data {
	name: &'static str,
	value: i32
}

impl fmt::Display for Data {
	fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
		write!(f, "Data: name={}, value={}", self.name, self.value)
	}
}

impl fmt::Debug for Data {
	fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
		write!(f, "*** debug : Data: name={}, value={}", self.name, self.value)
	}
}

#[derive(Debug)]
struct Data2 {
	name: &'static str,
	value: i32
}
