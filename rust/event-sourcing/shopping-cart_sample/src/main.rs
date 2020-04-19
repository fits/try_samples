
mod models;

fn main() {
    let cart_id1 = "cart-1";

    let cmd1 = models::CartCommand::Create { cart_id: cart_id1.to_string() };

    let (cart1, ev1) = models::Cart::Nothing.handle(&cmd1);
    println!("1 - {:?}, {:?}", cart1, ev1);

    let cmd2 = models::CartCommand::ChangeQty { 
        cart_id: cart_id1.to_string(), 
        item_id: "item-A".to_string(), 
        qty: 2
    };

    let (cart2, ev2) = cart1.handle(&cmd2);
    println!("2 - {:?}, {:?}", cart2, ev2);

    let cmd3 = models::CartCommand::Order {
        cart_id: cart_id1.to_string(),
        order_id: "order-1".to_string()
    };

    let (cart3, ev3) = cart2.handle(&cmd3);
    println!("3 - {:?}, {:?}", cart3, ev3);

    let (cart4, ev4) = cart3.handle(
        &models::CartCommand::Cancel { cart_id: cart_id1.to_string() }
    );

    println!("4 - {:?}, {:?}", cart4, ev4);

    let res = models::Cart::Nothing.restore(
        [ev1.unwrap(), ev2.unwrap(), ev3.unwrap()].iter()
    );

    println!("restored: {:?}", res);

    assert_eq!(cart4, res);
}
