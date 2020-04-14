
use chrono::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Debug, Deserialize, Serialize)]
struct Data {
    id: String,
    value: u32,
    date: DateTime<Local>,
}

fn main() {
    let d = Data {
        id: "d1".to_string(),
        value: 12,
        date: Local::now()
    };

    let json = serde_json::to_string(&d).unwrap();
    println!("{}", json);

    let rd: Data = serde_json::from_str(&json).unwrap();
    println!("{:?}", rd);
}
