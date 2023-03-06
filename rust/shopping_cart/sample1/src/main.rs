
mod cart;

use cart::*;

fn main() {
    let (c1, e1) = Cart::Nothing.create("cart-1".to_string()).unwrap();

    println!("state = {:?}, event = {:?}", c1, e1);

    let (c2, e2) = c1.change_qty(
        "item-1".to_string(), 
        3, 
        |c| Some(Item::Product { code: c.clone(), price: 100 })
    ).unwrap();

    println!("state = {:?}, event = {:?}", c2, e2);

    let (c3, e3) = c2.change_qty(
        "item-2".to_string(), 
        5, 
        |c| Some(Item::Product { code: c.clone(), price: 200 })
    ).unwrap();

    println!("state = {:?}, event = {:?}", c3, e3);

    let (c4, e4) = c3.change_qty(
        "item-2".to_string(), 
        0, 
        |c| Some(Item::Product { code: c.clone(), price: 200 })
    ).unwrap();

    println!("state = {:?}, event = {:?}", c4, e4);

    let (c5, e5) = c4.change_qty(
        "item-1".to_string(), 
        1, 
        |c| Some(Item::Product { code: c.clone(), price: 100 })
    ).unwrap();

    println!("state = {:?}, event = {:?}", c5, e5);
}
