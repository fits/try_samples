
use std::ops::Deref;

fn main() {
    let b = Box::new("sample".to_string());

    let r1 = b.capacity();
    let r2 = b.deref().capacity();

    assert_eq!(r1, r2);
    println!("{}", r1);
}