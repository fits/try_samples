
use prost::Message;

mod item {
    include!(concat!(env!("OUT_DIR"), "/sample.item.rs"));
}

fn create(id: &str, price: i32) -> item::Item {
    let mut d = item::Item::default();
    
    d.item_id = id.to_string();
    d.price = price;

    d
}

fn main() {
    let d = create("item-1", 100);

    println!("{:?}", d);

    let mut buf = Vec::with_capacity(d.encoded_len());

    d.encode(&mut buf).unwrap();

    println!("{:?}", buf);

    let d2 = item::Item::decode(buf.as_slice()).unwrap();

    println!("restored: {:?}", d2);
}
