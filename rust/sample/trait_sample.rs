
fn main() {
	let d = Data { name: "data", value: 4 };

	println!("{}", d.name);
	println!("{}", d.times());
}

struct Data {
	name: &'static str,
	value: i32
}

trait Sample {
	fn sample(&self) -> i32;

	fn times(&self) -> i32 {
		self.sample() * 2
	}
}

impl Sample for Data {
	fn sample(&self) -> i32 {
		self.value
	}
}
