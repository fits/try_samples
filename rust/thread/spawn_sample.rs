
use std::thread;

fn main() {
	let handle = thread::spawn(|| {
		println!("start spawn");

		let n = (1..10).fold(0, |acc, x| acc + x );

		println!("end spawn: {}", n);
	});

	println!("sample");

	let res = handle.join();

	println!("result : {:?}", res.ok());
}
