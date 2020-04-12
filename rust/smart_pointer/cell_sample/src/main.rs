
use std::cell::Cell;

#[derive(Debug)]
struct Data {
    id: String,
    value: Cell<u32>,
}

fn main() {
    let x = Cell::new(1);
    x.set(2);
    println!("x = {}", x.get());

    let y = &x;
    y.set(3);
    println!("x = {}", x.get());
    println!("y = {}", y.get());

    let d = Data { id: "d1".to_string(), value: Cell::new(0) };
    println!("{:?}", d);

    d.value.set(5);
    println!("{:?}", d);
}
