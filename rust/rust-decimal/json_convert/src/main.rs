use rust_decimal::prelude::*;

#[derive(Debug, serde::Deserialize, serde::Serialize)]
struct Data {
    name: String,
    value1: Decimal,
    value2: Option<Decimal>,
}

fn main() {
    let d1 = Data {
        name: "data1".to_string(),
        value1: dec!(1.23),
        value2: None,
    };

    println!("{:?}", d1);

    let s1 = serde_json::to_string(&d1).unwrap();
    println!("{}", s1);

    let r1 = serde_json::from_str::<Data>(&s1);
    println!("{:?}", r1);

    let d2 = Data {
        name: "data2".to_string(),
        value1: dec!(1.23),
        value2: Some(dec!(34.5)),
    };

    println!("{:?}", d2);

    let s2 = serde_json::to_string(&d2).unwrap();
    println!("{}", s2);

    let r2 = serde_json::from_str::<Data>(&s2);
    println!("{:?}", r2);
}
