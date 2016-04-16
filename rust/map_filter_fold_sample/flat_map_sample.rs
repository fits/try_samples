
fn main() {
	let size = |x: Option<String>| x.and_then(|s| Some(s.len()));

	let a = Some(String::from("test"));

	for r in size(a) {
		println!("{}", r);
	}

	println!("---");

	for r in size(None) {
		println!("{}", r);
	}

	println!("---");
}
