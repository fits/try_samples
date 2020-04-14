
use chrono::prelude::*;

#[derive(Debug)]
struct Data {
    id: String,
    value: u32,
    date: DateTime<Local>,
}

fn main() {
    let date1 = Local::now();

    println!("{:?}", date1);
    println!("{}", date1.to_rfc3339());

    let data = Data {
        id: "d1".to_string(),
        value: 12,
        date: Local::now()
    };

    println!("data = {:?}", data);
}
