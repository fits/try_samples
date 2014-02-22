fn main() {
	let d1 = Data { name: ~"data", value: 10 };
	let d2 = Data { name: ~"data", value: 10 };
	let d3 = Data { name: ~"data", value:  0 };
	let d4 = Data { name: ~"etc",  value:  5 };

	println!("d1 == d2 : {}", d1 == d2);
	println!("d1 == d2 : {}", d1.eq(&d2));
	println!("d1 == d3 : {}", d1 == d3);

	println!("-----")

	println!("{:?}", d1);
	println!("{}", d1.to_str());

	println!("-----")

	println!("times = {}", d1.times(3));

	println!("-----")

	d1.printValue();
	d3.printValue();

	println!("-----")

	let res = calc([d1, d2, d3, d4]);
	println!("calc = {}", res);
}

fn calc(list: &[Data]) -> int {
	list.iter().fold(1, |acc, v| acc * match v {
		// name = "data" で value の値が 0 より大きい場合
		&Data {name: ~"data", value: b} if b > 0 => b,
		// それ以外
		_ => 1
	})
}

#[deriving(Eq, ToStr)]
struct Data {
	name: ~str,
	value: int
}

// メソッドの定義
impl Data {
	fn printValue(&self) {
		match self.value {
			0 => println!("value: zero"),
			a @ _ => println!("value: {}", a)
		}
	}
}

// トレイトの定義
trait Sample {
	fn get_value(&self) -> int;

	fn times(&self, n: int) -> int {
		self.get_value() * n
	}
}

// トレイトの実装
impl Sample for Data {
	fn get_value(&self) -> int {
		self.value
	}
}
