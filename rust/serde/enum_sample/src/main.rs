use serde::{Deserialize, Serialize};
use serde_json::Result;

type StockId = String;

#[derive(Debug, Serialize, Deserialize)]
struct Stock {
    id: StockId,
    qty: i32,
}

#[derive(Debug, Serialize, Deserialize)]
#[serde(tag = "type")]
enum Event {
    Created { id: StockId },
    Updated { id: StockId, qty: i32 },
}

fn sample1() -> Result<()> {
    let s1 = Stock { id: "s1".to_string(), qty: 5 };

    let json = serde_json::to_string(&s1)?;
    println!("{}", json);

    let s2: Stock = serde_json::from_str(&json)?;
    println!("{:?}", s2);

    Ok(())
}

fn sample2() -> Result<()> {
    let ev1 = Event::Created { id: "s1".to_string() };
    let ev2 = Event::Updated { id: "s1".to_string(), qty: 10 };

    let json1 = serde_json::to_string(&ev1)?;
    let json2 = serde_json::to_string(&ev2)?;

    println!("{}", json1);
    println!("{}", json2);

    let ev1r: Event = serde_json::from_str(&json1)?;
    let ev2r: Event = serde_json::from_str(&json2)?;

    println!("{:?}", ev1r);
    println!("{:?}", ev2r);

    Ok(())
}

fn main() {
    sample1().unwrap();

    println!("-----");

    sample2().unwrap();
}
