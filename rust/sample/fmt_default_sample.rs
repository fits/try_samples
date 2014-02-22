
fn main() {
	let d = Data { name: ~"data1", value: 10 };

	println!("{}", d);
	println!("{:?}", d);
}

struct Data {
	name: ~str,
	value: int
}

impl std::fmt::Default for Data {
	fn fmt(obj: &Data, f: &mut std::fmt::Formatter) {
		write!(f.buf, "Data: name={}, value={}", obj.name, obj.value)
	}
}
