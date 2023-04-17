
mod cart;
use cart::*;

fn main() {
    let s1 = Cart::Nothing.create("cart-1".to_string()).unwrap();
    println!("{:?}", s1);

    let p1 = Product { catalog_id: "item-1".to_string(), unit_price: 100 };
    let p2 = Product { catalog_id: "item-2".to_string(), unit_price: 200 };

    let s2 = s1.add_item(p1.clone(), 1).unwrap();
    println!("{:?}", s2);
    
    let s3 = s2.add_item(p2.clone(), 2).unwrap();
    println!("{:?}", s3);

    let s4 = s3.add_item(p1.clone(), 3).unwrap();
    println!("{:?}", s4);

    let s5 = s4.begin_promotion().unwrap();
    println!("{:?}", s5);

    let d1 = SalesPromotion::Discount { promotion_id: "p1".to_string(), discount: 10 };

    let s6 = s5.add_promotion(d1).unwrap();
    println!("{:?}", s6);

    let s7 = s6.end_promotion().unwrap();
    println!("{:?}", s7);

    let s8 = s7.add_item(p2.clone(), 1).unwrap();
    println!("{:?}", s8);

    let s7a = s6.cancel_promotion().unwrap();
    println!("{:?}", s7a);
}
