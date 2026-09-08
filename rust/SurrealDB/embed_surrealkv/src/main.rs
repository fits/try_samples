use macuru::adt;

use surrealdb::Surreal;
use surrealdb::engine::local::{Db, SurrealKv};
use surrealdb::types::SurrealValue;

const ITEM_TABLE: &str = "items";

type ItemId = String;
type Amount = u64;

adt!(
    Item = Single | Bundle derive Debug, Clone, SurrealValue with ItemFunc {
        fn id(&self) -> &ItemId;
        fn unit_price(&self) -> Amount;
    }
);

#[derive(Debug, Clone, SurrealValue)]
pub struct Single {
    item_id: ItemId,
    name: String,
    unit_price: Amount,
}

#[derive(Debug, Clone, SurrealValue)]
pub struct Bundle {
    bundle_id: ItemId,
    name: String,
    unit_price: Amount,
    details: Vec<Single>,
}

impl ItemFunc for Single {
    fn id(&self) -> &ItemId {
        &self.item_id
    }

    fn unit_price(&self) -> Amount {
        self.unit_price
    }
}

impl ItemFunc for Bundle {
    fn id(&self) -> &ItemId {
        &self.bundle_id
    }

    fn unit_price(&self) -> Amount {
        self.unit_price
    }
}

#[tokio::main]
async fn main() -> surrealdb::Result<()> {
    let db = Surreal::new::<SurrealKv>("./db").await?;
    db.use_ns("example1").use_db("itemdb").await?;

    if let Ok(_) = insert_data(&db).await {
        println!("success insert");
    } else {
        println!("failed insert");
    }

    select_data(&db).await?;

    Ok(())
}

async fn insert_data(db: &Surreal<Db>) -> surrealdb::Result<()> {
    let s1 = Single {
        item_id: "A1".into(),
        name: "A1-item".into(),
        unit_price: 1200,
    };

    let s2 = Single {
        item_id: "B2".into(),
        name: "B2-item".into(),
        unit_price: 560,
    };

    let b1 = Bundle {
        bundle_id: "SET-1".into(),
        name: "SET-1".into(),
        unit_price: 1500,
        details: vec![s1.clone(), s2.clone()],
    };

    let item1: Item = s1.into();
    let item2: Item = s2.into();
    let item3: Item = b1.into();

    let r1 = db.create::<Option<Item>>((ITEM_TABLE, item1.id().as_str())).content(item1).await?;
    let r2 = db.create::<Option<Item>>((ITEM_TABLE, item2.id().as_str())).content(item2).await?;
    let r3 = db.create::<Option<Item>>((ITEM_TABLE, item3.id().as_str())).content(item3).await?;

    println!("created: {:?}", r1);
    println!("created: {:?}", r2);
    println!("created: {:?}", r3);

    Ok(())
}

async fn select_data(db: &Surreal<Db>) -> surrealdb::Result<()> {
    let rs1: Vec<surrealdb::types::Value> = db.select(ITEM_TABLE).await?;

    for r in rs1 {
        println!("{:?}", r);
    }

    println!("-----");

    let q = "SELECT * FROM items WHERE Single_.unit_price > 1000 OR Bundle_.unit_price > 1000";

    let mut rs2 = db.query(q).await?;

    for r in rs2.take::<Vec<Item>>(0)? {
        println!("{:?}", r);
    }

    Ok(())
}