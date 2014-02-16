
fn main() {
	printNum(0);
	printNum(3);
	printNum(7);

	println!("{}", updateNum(0));
	println!("{}", updateNum(3));
	println!("{}", updateNum(7));
}

fn printNum (x: int) {
	match x {
		0 => println!("zero"),
		1..5 => println!("one to five"),
		_ => println!("else")
	}
}

fn updateNum (x: int) -> int {
	match x {
		0 => x + 1,
		1..5 => x * 2,
		_ => x
	}
}
