
fn main() {
	let d1 = [1, 2, 3];

	println!("{:?}", d1);

	for x in &d1 {
		println!("res = {}", x);
	}

	print_each(&d1);
}

fn print_each(list: &[i32]) {
	for n in list {
		println!("v = {}", n);
	}
}
