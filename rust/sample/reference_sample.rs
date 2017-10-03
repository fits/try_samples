
fn sample(v: &Vec<i32>) {
	println!("sample: {:?}", v);
}

fn add(v: &mut Vec<i32>, i: i32) {
	v.push(i);
	println!("add: {:?}", v);
}


fn main() {
	let v = vec![1, 2];

	sample(&v);

	println!("v = {:?}", v);

	let mut a = vec![];

	add(&mut a, 3);

	println!("a = {:?}", a);
}
