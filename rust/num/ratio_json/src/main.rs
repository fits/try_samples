use num_rational::Ratio;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Deserialize, Serialize)]
struct Data {
    value: Ratio<i32>,
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let d = Data { value: Ratio::new(10, 3) };

    println!("{:?}", d);

    let v = serde_json::to_string(&d)?;

    println!("{}", v);

    let d2: Data = serde_json::from_str(&v)?;

    println!("{:?}", d2);

    Ok(())
}
