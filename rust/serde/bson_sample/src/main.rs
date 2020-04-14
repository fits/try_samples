
use serde::{Deserialize, Serialize};

type StockId = bson::oid::ObjectId;

#[derive(Debug, Deserialize, Serialize)]
struct Stock {
    #[serde(rename = "_id")]
    id: StockId,
    qty: i32,
}

#[derive(Debug, Deserialize, Serialize)]
#[serde(tag = "type")]
enum Event {
    Created { id: StockId },
    Updated { id: StockId, qty: i32 },
}

fn main() {
    let id = bson::oid::ObjectId::new().unwrap();

    let s = Stock { id: id.clone(), qty: 10 };

    let bs = bson::to_bson(&s).unwrap();
    println!("{:?}", bs);

    let sr: Stock = bson::from_bson(bs).unwrap();
    println!("{:?}", sr);

    let ev1 = Event::Created { id: id.clone() };
    let ev2 = Event::Updated { id: id.clone(), qty: 5 };

    let bs1 = bson::to_bson(&ev1).unwrap();
    let bs2 = bson::to_bson(&ev2).unwrap();

    println!("{:?}", bs1);
    println!("{:?}", bs2);

    if let bson::Bson::Document(doc) = &bs2 {
        println!("*** document = {:?}", doc);
        println!("*** document values = {:?}", doc.values().collect::<Vec<_>>());
    }

    let ev1r: Event = bson::from_bson(bs1).unwrap();
    let ev2r: Event = bson::from_bson(bs2).unwrap();

    println!("{:?}", ev1r);
    println!("{:?}", ev2r);
}
