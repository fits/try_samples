use move_based3::*;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let s1 = CartState::new("cart-1".into(), Anonymous.into())?;
    println!("s1 = {:?}", s1);

    let p1: Product = Product::new_single("A1".into(), 1100, 2)?;
    let p2: Product = Product::new_single("B2".into(), 330, 3)?;

    let w1: Location = Warehouse::new_logical("stock-1".into())?.into();

    let s2 = s1.add_item(p1.clone(), w1.clone())?;
    println!("s2 = {:?}", s2);

    let s3 = s2.add_item(p2.clone(), w1.clone())?;
    println!("s3 = {:?}", s3);

    let s4 = OrderState::new("order-1".into(), s3, Anonymous.into())?;
    println!("s4 = {:?}", s4);

    let s5 = s4.delivery(Address::new("123-4567".into(), "TEST ADDRESS".into())?, Some(550))?;
    println!("s5 = {:?}", s5);

    println!("s5 balance = {}", s5.balance());

    let s6 = s5.add_payment(Payment::new_credit("P-12".into(), 3740)?)?;
    println!("s6 = {:?}", s6);

    println!("s6 balance = {}", s6.balance());

    let s7 = s6.confirm()?;
    println!("s7 = {:?}", s7);

    Ok(())
}
