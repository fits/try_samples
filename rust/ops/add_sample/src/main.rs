
use std::ops::Add;

#[derive(Debug, Copy, Clone)]
struct Data(i32);

impl Add for Data {
    type Output = Self;

    fn add(self, other: Self) -> Self::Output {
        Self(self.0 + other.0)
    }
}

fn main() {
    let d1 = Data(5);
    let d2 = Data(3);

    let d3 = d1 + d2;

    println!("{:?}", d3);
}
