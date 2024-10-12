use std::collections::HashMap;

fn main() {
    let m = HashMap::from([
        ("item-1", 3),
        ("item-2", 1),
        ("item-3", 9),
        ("item-4", 4),
        ("item-5", 2),
        ("item-6", 7),
    ]);

    for (k, v) in &m {
        println!("{},{}", v, k);
    }

    println!("-----");

    let mut s = m.into_iter().collect::<Vec<_>>();
    s.sort_by(|a, b| b.1.cmp(&a.1));

    for (k, v) in s {
        println!("{},{}", v, k);
    }
}
