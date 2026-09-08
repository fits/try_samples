
use surrealdb::Surreal;
use surrealdb::engine::local::{Db, Mem};
use surrealdb::types::SurrealValue;

const ITEM_TABLE: &str = "items";

type ItemId = String;
type Amount = u64;

#[derive(Debug, Clone, SurrealValue)]
enum Item {
    Single(SingleItem),
    Bundle(BundleItem),
}

#[derive(Debug, Clone, SurrealValue)]
struct SingleItem {
    item_id: ItemId,
    unit_price: Amount,
}

#[derive(Debug, Clone, SurrealValue)]
struct BundleItem {
    bundle_id: ItemId,
    unit_price: Amount,
    details: Vec<SingleItem>,
}

impl Item {
    fn id(&self) -> &String {
        match self {
            Self::Single(x) => &x.item_id,
            Self::Bundle(x) => &x.bundle_id,
        }
    }
}

#[tokio::main]
async fn main() -> surrealdb::Result<()> {
    let db = Surreal::new::<Mem>(()).await?;
    db.use_ns("example1").use_db("itemdb").await?;

    insert_data(&db).await?;

    println!("-----");

    select_data(&db).await?;

    Ok(())
}

async fn insert_data(db: &Surreal<Db>) -> surrealdb::Result<()> {
    let s1 = SingleItem {
        item_id: "A1".into(),
        unit_price: 1200,
    };

    let s2 = SingleItem {
        item_id: "B2".into(),
        unit_price: 560,
    };

    let b1 = BundleItem {
        bundle_id: "SET-1".into(),
        unit_price: 1500,
        details: vec![s1.clone(), s2.clone()],
    };

    let item1 = Item::Single(s1.clone());
    let item2 = Item::Single(s2.clone());
    let item3 = Item::Bundle(b1.clone());

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

    let q = "SELECT * FROM items WHERE Single.unit_price > 1000 OR Bundle.unit_price > 1000";

    let mut rs2 = db.query(q).await?;

    for r in rs2.take::<Vec<Item>>(0)? {
        println!("{:?}", r);
    }

    Ok(())
}