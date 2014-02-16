
fn main() {
	printNum(0);
	printNum(3);
	printNum(7);

	printNum2(0);
	printNum2(3);
	printNum2(7);

	println!("{}", updateNum(0));
	println!("{}", updateNum(3));
	println!("{}", updateNum(7));

	println!("{}", updateNum2(3));
	println!("{}", updateNum2(7));
}

fn printNum (x: int) {
	match x {
		0 => println!("zero"),
		1..5 => println!("one to five"),
		_ => println!("else")
	}
}

fn printNum2 (x: int) {
	match x {
		0 => { println!("zero") }
		1..5 => { println!("one to five") }
		_ => { println!("else") }
	}
}

fn updateNum (x: int) -> int {
	match x {
		0 => x + 1,
		1..5 => x * 2,
		_ => x
	}
}

fn updateNum2 (x: int) -> int {
	match x {
		a @ 1..5 => a * 2,
		_ => 0
	}
}
