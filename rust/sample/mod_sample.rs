
fn main() {
	let d = sample::Data { name: ~"data", value: 4 };

	println!("{}", d.plus(3));
	println!("{}", sample::sub::show(&d));
}

mod sample {
	pub struct Data {
		name: ~str,
		value: int
	}

	impl Data {
		pub fn plus(&self, v: int) -> int {
			self.value + v
		}
	}

	pub mod sub {
		pub fn show(data: &::sample::Data) -> ~str {
			format!("{}-{}", data.name, data.value)
		}
	}
}
