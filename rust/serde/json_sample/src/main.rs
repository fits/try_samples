use num::{BigInt, BigRational};
use serde::{Deserialize, Serialize};
use serde_json::Result;

use std::collections::HashMap;

type Id = String;
type AttrKey = String;
type Attrs = HashMap<AttrKey, AttrValue>;

#[derive(Debug, Clone, PartialEq, Deserialize, Serialize)]
enum AttrValue {
    Text(String),
    Integer(BigInt),
}

#[derive(Debug, Clone, PartialEq, Deserialize, Serialize)]
struct Data {
    id: Id,
    attrs: Attrs,
    value: BigRational,
}

fn main() -> Result<()> {
    let d = Data {
        id: "data-1".into(),
        attrs: HashMap::from([
            ("category".into(), AttrValue::Text("A1".into())),
            ("no".into(), AttrValue::Integer(100.into())),
        ]),
        value: BigRational::from_integer(123.into()),
    };

    let s = serde_json::to_string(&d)?;
    println!("{}", s);

    let r: Data = serde_json::from_str(&s)?;

    println!("{:?}", r);

    assert!(d == r);

    Ok(())
}
