
fn main() {
	let d = Data { name: ~"data", value: 4 };

	println!("{}", d.times());
}

struct Data {
	name: ~str,
	value: int
}

trait Sample {
	fn sample(&self) -> int;

	fn times(&self) -> int {
		self.sample() * 2
	}
}

impl Sample for Data {
	fn sample(&self) -> int {
		self.value
	}
}
