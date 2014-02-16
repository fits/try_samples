
fn main() {
	let d = Data { name: ~"data1", value: 10 };
	let mut d2 = Data { name: ~"data2", value: 5};

	println!("{:?}", d);
	println!("{:?}", d2);

	d2.name.push_str("-up");
	d2.value += 3;

	println!("{:?}", d2);
}

struct Data {
	name: ~str,
	value: int
}
