use std::fmt::Display;

#[derive(Debug, Clone)]
pub struct Item<T, const N: usize> {
    pub data: [T; N],
}

impl<T, const N: usize> Item<T, N> {
    pub fn new(d: [T; N]) -> Self {
        Self { data: d }
    }
}

trait ShowItem {
    fn print_data(&self);
}

impl<T> ShowItem for Item<T, 2>
where
    T: Display,
{
    fn print_data(&self) {
        println!("first={}, second={}", self.data[0], self.data[1]);
    }
}

fn main() {
    let d1 = Item::new([1]);
    println!("{:?}", d1);

    let d2 = Item::new([1, 2]);
    println!("{:?}", d2);

    d2.print_data();
}
