
fn main() {
	print_num(0);
	print_num(3);
	print_num(5);
	print_num(7);

	print_num2(0);
	print_num2(3);
	print_num2(5);
	print_num2(7);

	println!("{}", update_num(0));
	println!("{}", update_num(3));
	println!("{}", update_num(5));
	println!("{}", update_num(7));

	println!("{}", update_num2(3));
	println!("{}", update_num2(5));
	println!("{}", update_num2(7));
}

fn print_num (x: i32) {
	match x {
		0 => println!("zero"),
		1...5 => println!("one to five"),
		_ => println!("else")
	}
}

fn print_num2 (x: i32) {
	match x {
		0 => { println!("zero") }
		1...5 => { println!("one to five") }
		_ => { println!("else") }
	}
}

fn update_num (x: i32) -> i32 {
	match x {
		0 => x + 1,
		1...5 => x * 2,
		_ => x
	}
}

fn update_num2 (x: i32) -> i32 {
	match x {
		a @ 1...5 => a * 2,
		_ => 0
	}
}
