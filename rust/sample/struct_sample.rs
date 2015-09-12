
fn main() {
	let d = Data { name: "data1".to_string(), value: 10 };
	let mut d2 = Data { name: "data2".to_string(), value: 5};

	println!("{:?}", d);
	println!("{:?}", d2);

	d2.name.push_str("-up");
	d2.value += 3;

	println!("{:?}", d2);
}

#[derive(Debug)]
struct Data {
	name: String,
	value: i32
}
