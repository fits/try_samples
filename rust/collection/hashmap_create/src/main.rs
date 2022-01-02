
use std::collections::HashMap;

fn main() {
    let d1 = HashMap::from([
        ("name", "test1"),
    ]);

    println!("{:?}", d1);

    let d2 = HashMap::from([
        ("item-1".to_string(), 1),
        ("item-2".to_string(), 2),
        ("item-3".to_string(), 3),
    ]);

    println!("{:?}", d2);
}
