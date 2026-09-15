use std::env;
use std::str::FromStr;
use std::time::SystemTime;

use surrealdb::Surreal;
use surrealdb::engine::local::SurrealKv;
use surrealdb::types::{SurrealValue, Value};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

type ItemCode = String;
type Quantity = u32;

#[derive(Debug, Clone, SurrealValue)]
struct Item {
    code: ItemCode,
    qty: Quantity,
}

const LIMIT: u32 = 10;

#[tokio::main]
async fn main() -> Result<()> {
    let v_stamp = env::args()
        .skip(1)
        .next()
        .and_then(|x| u64::from_str(&x).ok())
        .unwrap_or_default()
        + 1;

    let db = Surreal::new::<SurrealKv>("./db").await?;
    db.use_ns("example").use_db("catalog").await?;

    let mut rs = db
        .query("DEFINE TABLE IF NOT EXISTS items CHANGEFEED 1h")
        .await?;

    rs.take::<Value>(0)?;

    let n = SystemTime::now()
        .duration_since(SystemTime::UNIX_EPOCH)?
        .as_millis();

    let item = Item {
        code: format!("item-{}", n),
        qty: 1,
    };

    let r: Option<Value> = db.create("items").content(item).await?;
    println!("create: {:?}", r);

    if let Some(x) = r {
        let id = x.get("id").as_record().unwrap();

        db.query("UPDATE $id SET qty = qty + 1")
            .bind(("id", id.clone()))
            .await?;
    }

    /*
     * "SHOW" statement is not support bind parameters in SurrealDB 3.2.4.
     * ref surrealdb/core/src/syn/parser/stmt/mod.rs, Parser::parse_show_stmt
     */
    let q = format!(
        "SHOW CHANGES FOR TABLE items SINCE {} LIMIT {}",
        v_stamp, LIMIT
    );

    let mut rs = db.query(q).await?;

    let v: Value = rs.take(0)?;

    if let Value::Array(xs) = v {
        for x in xs {
            println!("{:?}", x);
        }
    }

    Ok(())
}
