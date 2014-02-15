extern mod std;

fn main() {
	let list = [1, 2, 3];
	let mut res = list.iter().map(|&x| x * 10);

	for &x in list.iter() {
		println(format!("{}", x));
	}

	for x in res {
		println(format!("{}", x));
	}
}
