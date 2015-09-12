
fn main() {
	fn plus(x: i32) -> i32 {
		x + 2
	}

	println!("{}", plus(1));
	println!("{}", times(2));
}

fn times(x: i32) -> i32 {
	x * 3
}
