
fn sample(v: &Vec<i32>) {
	println!("sample.v: {:?}", v);
}

fn main() {
	let v = vec![1, 2];

	sample(&v);

	println!("{:?}", v);
}
