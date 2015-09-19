
fn main() {
	let d1 = [1, 2, 3];

	println!("{:?}", d1);

	for x in &d1 {
		println!("res = {}", x);
	}
}
