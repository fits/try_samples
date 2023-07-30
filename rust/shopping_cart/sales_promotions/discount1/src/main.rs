mod models;

use models::*;

fn main() {
    let p = 1205;

    let d = Discount::rate_with(10, Rounding::Ceil);

    println!("{:?}", d);
    println!("{}", d.discount(&p));
}
