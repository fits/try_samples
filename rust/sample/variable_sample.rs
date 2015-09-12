
fn main() {
	static COUNT: i32 = 100;
	let x = 0;
	let mut y = 0;

	y += 1;

	let z: i32 = 50;

	println!("x={}, y={}, z={}", x, y, z);
	println!("{}", COUNT * (y + 3));
}
