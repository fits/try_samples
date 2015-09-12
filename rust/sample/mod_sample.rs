
fn main() {
	let d = sample::Data { name: "data", value: 4 };

	println!("{}", d.plus(3));
	println!("{}", sample::sub::show(&d));
}

mod sample {
	pub struct Data {
		pub name: &'static str,
		pub value: i32
	}

	impl Data {
		pub fn plus(&self, v: i32) -> i32 {
			self.value + v
		}
	}

	pub mod sub {
		pub fn show(data: &::sample::Data) -> String {
			format!("{}-{}", data.name, data.value)
		}
	}
}
