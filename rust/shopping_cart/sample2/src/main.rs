
mod cart;
use cart::*;

fn main() {
    let s1 = Cart::Nothing.create("cart-1".to_string()).unwrap();
    println!("{:?}", s1);

    let p1 = Product { catalog_id: "item-1".to_string(), unit_price: 100 };
    let p2 = Product { catalog_id: "item-2".to_string(), unit_price: 200 };

    let s2 = s1.add_item(p1.clone(), 1).unwrap();
    println!("{:?}", s2);
    
    let s3 = s2.add_item(p2, 2).unwrap();
    println!("{:?}", s3);

    let s4 = s3.add_item(p1, 3).unwrap();
    println!("{:?}", s4);
}
