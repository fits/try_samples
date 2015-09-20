
fn main() {
	let d = Data { name: "data1".to_string(), value: 10 };

	println!("{}", d.to_string());
}

struct Data {
	name: String,
	value: i32
}

impl ToString for Data {
	fn to_string(&self) -> String {
		format_args!("Data name:{}, value:{}", self.name, self.value).to_string()
	}
}
