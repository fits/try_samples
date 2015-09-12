
fn main() {
	let d = Data { value: 4 };

	println!("{}", d.times());
	println!("{}", d.plus(3));
	println!("{}", d.plus2());
}

struct Data {
	value: i32
}

impl Data {
	fn times(&self) -> i32 {
		self.value * 2
	}

	fn plus(&self, v: i32) -> i32 {
		self.value + v
	}
}

impl Data {
	fn plus2(&self) -> i32 {
		self.plus(2)
	}
}
