
fn main() {
	let d = Data { name: ~"data", value: 4 };

	println!("{}", d.times());
	println!("{}", d.plus(3));
	println!("{}", d.plus2());
}

struct Data {
	name: ~str,
	value: int
}

impl Data {
	fn times(&self) -> int {
		self.value * 2
	}

	fn plus(&self, v: int) -> int {
		self.value + v
	}
}

impl Data {
	fn plus2(&self) -> int {
		self.plus(2)
	}
}
