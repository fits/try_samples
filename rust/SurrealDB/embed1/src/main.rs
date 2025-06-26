use surrealdb::Surreal;
use surrealdb::engine::local::Mem;
use surrealdb::sql::Thing;

use serde::{Deserialize, Serialize};

#[allow(dead_code)]
#[derive(Debug, Deserialize, Serialize)]
struct Item {
    #[serde(skip_serializing)]
    id: Option<Thing>,
    name: String,
    price: usize,
    attrs: Attribute,
    variants: Vec<Variation>,
}

#[derive(Debug, Deserialize, Serialize)]
struct Attribute {
    category: Option<String>,
}

#[derive(Debug, Deserialize, Serialize)]
struct Variation {
    color: String,
}

#[tokio::main]
async fn main() -> surrealdb::Result<()> {
    let db = Surreal::new::<Mem>(()).await?;
    db.use_ns("test1").use_db("db").await?;

    let v1: Option<Item> = db
        .create("items")
        .content(Item {
            id: None,
            name: "item-1".to_string(),
            price: 1200,
            attrs: Attribute {
                category: Some("A1".to_string()),
            },
            variants: vec![],
        })
        .await?;

    println!("* created: {:?}", v1);

    db.create::<Option<Item>>(("items", "i2"))
        .content(Item {
            id: None,
            name: "item-2".to_string(),
            price: 2340,
            attrs: Attribute {
                category: Some("B2".to_string()),
            },
            variants: vec![
                Variation {
                    color: "white".to_string(),
                },
                Variation {
                    color: "black".to_string(),
                },
            ],
        })
        .await?;

    db.create::<Option<Item>>(("items", "i3"))
        .content(Item {
            id: None,
            name: "item-3".to_string(),
            price: 450,
            attrs: Attribute {
                category: Some("B2".to_string()),
            },
            variants: vec![Variation {
                color: "red".to_string(),
            }],
        })
        .await?;

    db.create::<Option<Item>>(("items", "i4"))
        .content(Item {
            id: None,
            name: "item-4".to_string(),
            price: 56,
            attrs: Attribute {
                category: Some("C1".to_string()),
            },
            variants: vec![
                Variation {
                    color: "black".to_string(),
                },
                Variation {
                    color: "red".to_string(),
                },
            ],
        })
        .await?;

    let r1: Vec<Item> = db.select("items").await?;
    println!("* select all = {:?}", r1);

    let r2: Option<Item> = db.select(("items", "i2")).await?;
    println!("* select one = {:?}", r2);

    let q1 = "SELECT * FROM items WHERE attrs.category = 'A1'";
    let q2 = "SELECT * FROM items WHERE variants[WHERE color = 'black']";

    let mut res = db.query(q1).query(q2).await?;

    let r31: Vec<Item> = res.take(0)?;
    println!("* q1 = {:?}", r31);

    let r32: Vec<Item> = res.take(1)?;
    println!("* q2 = {:?}", r32);

    Ok(())
}
