
mod models;

use models::*;

fn main() {
    let c = Command::Create("s1".to_string());
    let s = Stock::None.action(&c);
    println!("{:?}", s);

    let c2 = Command::Update(10);
    let s2 = s.unwrap_or(Stock::None).action(&c2);
    println!("{:?}", s2);

    let s2_a = s2.clone().unwrap_or(Stock::None).action(&c2);
    println!("{:?}", s2_a);

    let c3 = Command::Update(0);
    let s3 = s2.unwrap_or(Stock::None).action(&c3);
    println!("{:?}", s3);
}
