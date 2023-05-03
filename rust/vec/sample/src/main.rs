use std::time::SystemTime;

type Id = String;
type Quantity = u32;

#[derive(Debug, Clone)]
struct Item {
    id: Id,
    qty: Quantity,
}

fn add_item(src: &Vec<Item>, item: Item) -> Vec<Item> {
    let mut res = vec![];
    let mut upd = false;

    for v in src {
        if v.id == item.id {
            res.push(Item { id: v.id.clone(), qty: v.qty + item.qty });
            upd = true;
        } else {
            res.push(v.clone());
        }
    }

    if !upd {
        res.push(item);
    }

    res
}

fn add_item2(src: &Vec<Item>, item: Item) -> Vec<Item> {
    let it = (vec![], false);

    let (mut rs, upd) = src
        .iter()
        .fold(it, |acc, v|{
            let mut rs = acc.0;

            if v.id == item.id {
                rs.push(Item { id: v.id.clone(), qty: v.qty + item.qty });
            } else {
                rs.push(v.clone());
            }

            (rs, acc.1 || v.id == item.id)
        });

    if !upd {
        rs.push(item);
    }

    rs
}

fn add_item3(src: &Vec<Item>, item: Item) -> Vec<Item> {
    let mut upd = false;

    let mut res = src
        .iter()
        .map(|v|
            if v.id == item.id {
                upd = true;
                Item { id: v.id.clone(), qty: v.qty + item.qty }
            } else {
                v.clone()
            }
        )
        .collect::<Vec<_>>();

    if !upd {
        res.push(item);
    }

    res
}

fn add_item4(src: &Vec<Item>, item: Item) -> Vec<Item> {
    let exists = src.iter().any(|v| v.id == item.id);

    if exists {
        src
            .iter()
            .map(|v|
                if v.id == item.id {
                    Item { id: v.id.clone(), qty: v.qty + item.qty }
                } else {
                    v.clone()
                }
            )
            .collect::<Vec<_>>()
    } else {
        [src.clone(), vec![ item ]].concat()
    }
}

fn test(n: u32, f: fn(&Vec<Item>, Item) -> Vec<Item>) {
    let now = SystemTime::now();

    for _ in 1..=n {
        let r1 = vec![];

        let r2 = f(&r1, Item { id: "item-1".to_owned(), qty: 1 });    
        let r3 = f(&r2, Item { id: "item-2".to_owned(), qty: 2 });    
        let r4 = f(&r3, Item { id: "item-1".to_owned(), qty: 3 });
        let r5 = f(&r4, Item { id: "item-3".to_owned(), qty: 4 });
        let _r6 = f(&r5, Item { id: "item-3".to_owned(), qty: 5 });
    }

    println!("time = {} micros", now.elapsed().unwrap().as_micros());
}

fn main() {
    let n = 100;

    test(n, add_item);
    println!("-----");
    test(n, add_item2);
    println!("-----");
    test(n, add_item3);
    println!("-----");
    test(n, add_item4);
}
