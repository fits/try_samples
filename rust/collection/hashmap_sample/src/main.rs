
use std::collections::HashMap;
use std::iter::FromIterator;

#[derive(Debug, Default, Clone)]
struct Data {
    value: i32,
}

fn main() {
    let mut store = HashMap::<String, Data>::new();

    let a1 = "a1".to_string();
    let a1_v = Data { value: 1 };

    store.insert(a1, a1_v);
    store.entry("a1".to_string()).or_insert(Data { value: 2 });

    store.insert("b2".to_string(), Data { value: 20 });
    store.insert("c3".to_string(), Data { value: 300 });

    for (k, v) in &store {
        println!("key={}, value={:?}", k, v);
    }

    let print_value = |k: &str| match store.get(k) {
        Some(v) => println!("{} : {:?}", k, v),
        _ => println!("{} : none", k),
    };

    print_value("a1");
    print_value("d1");

    let print_value2 = |k: &str| {
        let v = store.get(k).cloned().unwrap_or_default();
        println!("{} - {:?}", k, v);
    };

    print_value2("b2");
    print_value2("d2");

    let vs = store.values().collect::<Vec<_>>();
    println!("values: {:?}", vs);

    let es = Vec::from_iter(store);
    println!("entries: {:?}", es);
}
