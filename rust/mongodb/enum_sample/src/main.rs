
use bson::Bson;
use bson::oid::ObjectId;
use chrono::prelude::*;
use mongodb::Client;
use serde::{Deserialize, Serialize};

type EventId = ObjectId;
type StockId = String;

#[derive(Debug, Deserialize, Serialize)]
#[serde(tag = "type")]
enum StockEvent {
    Created { 
        #[serde(rename = "_id")]
        id: EventId, 
        stock_id: StockId,
        date: DateTime<Local>,
    },
    Updated { 
        #[serde(rename = "_id")]
        id: EventId, 
        stock_id: StockId, 
        qty: i32,
        date: DateTime<Local>,
    },
}

fn main() {
    let mongo = Client::with_uri_str("mongodb://localhost").unwrap();
    let col = mongo.database("sample").collection("event");

    let ev1 = StockEvent::Created {
        id: ObjectId::new().unwrap(), 
        stock_id: "s1".to_string(), 
        date: Local::now()
    };

    if let Bson::Document(doc) = bson::to_bson(&ev1).unwrap() {
        let r = col.insert_one(doc, None).unwrap();

        if let Bson::ObjectId(id) = r.inserted_id {
            println!("insert id: {}", id);
        }
    }

    let ev2 = StockEvent::Updated {
        id: ObjectId::new().unwrap(), 
        stock_id: "s1".to_string(),
        qty: 10, 
        date: Local::now()
    };

    if let Bson::Document(doc) = bson::to_bson(&ev2).unwrap() {
        let r = col.insert_one(doc, None).unwrap();
        println!("{:?}", r);
    }

    let ds = col.find(None, None).unwrap();

    let rs: Vec<_> = ds.filter_map(|doc|
        match doc {
            Ok(d) => 
                bson::from_bson::<StockEvent>(Bson::Document(d)).ok(),
            Err(e) => {
                println!("ERROR: {:?}", e);
                None
            },
        }
    ).collect();

    println!("{:?}", rs);
}
