use rust_decimal::prelude::*;

fn main() {
    let d1 = Decimal::new(123, 2);
    println!("{}", d1);

    let d2 = dec!(1.23);
    println!("{}", d2);

    assert_eq!(d1, d2);

    let d3 = d1 / dec!(0.3);
    println!("{}", d3);

    let d4 = d1 * dec!(2) / Decimal::from_str("0.7").unwrap();
    println!("{}", d4);
}
