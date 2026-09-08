use surrealdb::Surreal;
use surrealdb::engine::local::Mem;
use surrealdb::types::{RecordId, SurrealValue};

use std::fs::File;
use std::io::{BufRead, BufReader};

#[derive(Debug, SurrealValue)]
struct Document {
    id: RecordId,
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    let file = "./data/items.jsonl";

    let db = Surreal::new::<Mem>(()).await?;
    db.use_ns("test1").use_db("db").await?;

    for line in BufReader::new(File::open(file)?).lines() {
        if let Ok(s) = line {
            let v: serde_json::Value = serde_json::from_str(&s)?;
            let r: Option<serde_json::Value> = db.create("items").content(v).await?;

            println!("* registerd: {:?}", r);
        }
    }

    let q1 = "SELECT * FROM items WHERE id < items:4";
    let q2 = "SELECT * FROM items WHERE attrs.category = 'A1'";
    let q3 = "SELECT * FROM items WHERE variants[WHERE color = 'white']";

    let mut res = db.query(q1).query(q2).query(q3).await?;

    println!("* q1 = {:?}", res.take::<Vec<Document>>(0)?);
    println!("* q2 = {:?}", res.take::<Vec<Document>>(1)?);
    println!("* q3 = {:?}", res.take::<Vec<Document>>(2)?);

    println!("-----");

    let mut res2 = db.query(q1).query(q2).query(q3).await?;

    println!("* q1 = {:?}", res2.take::<Vec<RecordId>>((0, "id"))?);
    println!("* q2 = {:?}", res2.take::<Vec<RecordId>>((1, "id"))?);
    println!("* q3 = {:?}", res2.take::<Vec<RecordId>>((2, "id"))?);

    println!("-----");

    let a = "SELECT id, attrs.code as code, attrs, variants FROM items WHERE id < items:4";
    let b = "SELECT id, name, attrs.category FROM items WHERE attrs.category = 'A1'";
    let c = "SELECT id, name FROM items WHERE variants[WHERE color = 'white']";

    let mut res3 = db.query(a).query(b).query(c).await?;

    let a_res: surrealdb::types::Value = res3.take(0)?;

    println!("* a = {}, debug = {:?}", a_res.clone().into_json_value(), a_res);
    println!("* b = {}", res3.take::<surrealdb::types::Value>(1)?.into_json_value());
    println!("* c = {}", res3.take::<surrealdb::types::Value>(2)?.into_json_value());

    let mut res4 = db.query(a).await?;

    println!("a = {:?}", res4.take::<Vec<RecordId>>((0, "id"))?);

    Ok(())
}
