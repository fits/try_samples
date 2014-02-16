
fn main() {
	fn plus(x: int) -> int {
		x + 2
	}

	println!("{}", plus(1));
	println!("{}", times(2));
}

fn times(x: int) -> int {
	x * 3
}
