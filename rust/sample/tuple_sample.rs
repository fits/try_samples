
fn main() {
	let t1: (~str, int) = (~"tuple1", 1);
	println!("{:?}", t1);

	match t1 {
		(_, v) => println!("{}", v)
	}

	let t2 = DataTuple(~"tuple2", 2);
	println!("{:?}", t2);

	match t2 {
		DataTuple(s, _) => println!("{}", s)
	}
}

struct DataTuple(~str, int);
