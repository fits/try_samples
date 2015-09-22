
macro_rules! sample {
	( $( $x:expr ) * ) => {
		{
			$(
				println!("{:?}", $x);
			)*
		}
	};
}

fn main() {
	sample!(1 2 3 + 5 "test");
}
