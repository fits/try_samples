use surrealdb::Surreal;
use surrealdb::engine::local::Mem;
use surrealdb::sql::Thing;

use serde::Deserialize;
use serde_json::Value;

use std::fs::File;
use std::io::{BufRead, BufReader};

#[allow(dead_code)]
#[derive(Debug, Deserialize)]
struct Document {
    id: Thing,
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    let db = Surreal::new::<Mem>(()).await?;
    db.use_ns("sample").use_db("db").await?;

    let file = "./data/items.jsonl";

    for line in BufReader::new(File::open(file)?).lines() {
        if let Ok(s) = line {
            let v: Value = serde_json::from_str(&s)?;
            let r: Option<Document> = db.create("items").content(v).await?;

            println!("* registered: {:?}", r);
        }
    }

    let a = "SELECT id, attrs.code as code, attrs, variants FROM items WHERE id < items:4";
    let b = "SELECT id, name, attrs.category FROM items WHERE attrs.category = 'A1'";
    let c = "SELECT id, name FROM items WHERE variants[WHERE color = 'white']";

    let mut res = db.query(a).query(b).query(c).await?;

    let a_res: surrealdb::Value = res.take(0)?;
    println!("(a) = {}", a_res.to_string());

    println!("(b) = {}", res.take::<surrealdb::Value>(1)?.to_string());
    println!("(c) = {}", res.take::<surrealdb::Value>(2)?.to_string());

    Ok(())
}
