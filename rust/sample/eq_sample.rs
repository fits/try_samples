
fn main() {
	let d1 = Data { name: ~"data", value: 10 };
	let d2 = Data { name: ~"data", value: 10 };
	let d3 = Data { name: ~"data", value: 15 };

	println!("d1 == d2 : {}", d1 == d2);
	println!("d1 == d3 : {}", d1 == d3);
	println!("{:?}", d1);

	let d2_1 = Data2 { name: ~"data", value: 10 };
	let d2_2 = Data2 { name: ~"data", value: 10 };
	let d2_3 = Data2 { name: ~"data", value: 15 };

	println!("d2_1 == d2_2 : {}", d2_1 == d2_2);
	println!("d2_1 == d2_3 : {}", d2_1 == d2_3);
	println!("{}", d2_1.to_str());
}

struct Data {
	name: ~str,
	value: int
}

impl Eq for Data {
	fn eq(&self, y: &Data) -> bool {
		self.name == y.name && self.value == y.value
	}
}

#[deriving(Eq, ToStr)]
struct Data2 {
	name: ~str,
	value: int
}

