#![feature(trait_alias)]

use std::ops::Add;

trait Id = Eq + Clone;
trait Quantity = Add<Output = Self> + Clone;

#[derive(Clone, Copy, Debug)]
struct Stock<I, Q> {
    id: I,
    qty: Q,
}

impl<I: Id, Q: Quantity> Add for Stock<I, Q> {
    type Output = Self;

    fn add(self, other: Self) -> Self::Output {
        if self.id == other.id {
            Self {
                id: self.id.clone(),
                qty: self.qty + other.qty,
            }
        } else {
            self
        }
    }
}

fn plus<T: Add<Output = T>>(a: T, b: T) -> T {
    a + b
}

fn main() {
    let a = Stock { id: "s1", qty: 10 };
    let b = Stock { id: "s1", qty: 5 };
    let c = Stock { id: "s2", qty: 3 };

    println!("a + b = {:?}", a + b);
    println!("plus(a, b) = {:?}", plus(a, b));

    println!("a + c = {:?}", a + c);
}
