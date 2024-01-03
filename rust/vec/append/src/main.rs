
type Id = String;
type Quantity = u32;

#[derive(Debug, Clone)]
struct Item {
    id: Id,
    qty: Quantity,
}

fn add_item(mut items: Vec<Item>, id: Id, qty: Quantity) -> Vec<Item> {
    match items.iter().position(|v| v.id == id) {
        Some(index) => {
            if qty == 0 {
                items.remove(index);
            } else {
                items[index].qty = qty;
            }
        }
        None => {
            if qty > 0 {
                items.push(Item { id, qty });
            }
        }
    };

    items
}

fn main() {
    let r1 = vec![];

    let r2 = add_item(r1, "item-1".to_string(), 1);
    println!("{:?}", r2);

    let r3 = add_item(r2, "item-2".to_string(), 2);
    println!("{:?}", r3);

    let r4 = add_item(r3, "item-3".to_string(), 3);
    println!("{:?}", r4);

    let r5 = add_item(r4, "item-1".to_string(), 5);
    println!("{:?}", r5);

    let r6 = add_item(r5, "item-2".to_string(), 0);
    println!("{:?}", r6);

    let r7 = add_item(r6, "item-4".to_string(), 0);
    println!("{:?}", r7);

    let r8 = add_item(r7, "item-1".to_string(), 0);
    println!("{:?}", r8);
}
