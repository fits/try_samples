
use num::{BigInt, Integer};

fn main() {
    let n = BigInt::from(1234);
    let n2 = &n * 5 / 100;

    println!("{}", n);
    println!("{}", n2);

    let n3 = (&n * 5i32).div_ceil(&100.into());
    println!("{}", n3);

    let n4 = (&n * 5i32).div_floor(&BigInt::from(100));
    println!("{}", n4);

    println!("{:?}", BigInt::from(123).div_rem(&10.into()));
    println!("{:?}", BigInt::from(123).div_rem(&100.into()));
    println!("{:?}", BigInt::from(154).div_rem(&10.into()));

    println!("{:?}", BigInt::from(11).div_rem(&3.into()));
}
