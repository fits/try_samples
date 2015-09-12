
fn main() {
	let list = [1, 2, 3];
	let res = list.iter().map(|&x| x * 10);
	let res2: Vec<_> = list.iter().map(|&x| x * 10).collect();

	for &x in list.iter() {
		println!("{}", x);
	}

	for x in res {
		println!("{}", x);
	}

	for &x in res2.iter() {
		println!("{}", x);
	}

	println!("{:?}", list);
//	println!("{:?}", res);
	println!("{:?}", res2);
}
