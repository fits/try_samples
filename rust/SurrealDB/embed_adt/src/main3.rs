use serde::{Deserialize, Serialize};
use surrealdb::Surreal;
use surrealdb::engine::local::{Db, Mem};
use surrealdb::types::{RecordId, SerdeWrapper, SurrealValue};

const ITEM_TABLE: &str = "items";

type ItemId = String;
type Amount = u64;

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "type")]
enum Item {
    Single(SingleItem),
    Bundle(BundleItem),
}

#[derive(Debug, Clone, Serialize, Deserialize)]
struct SingleItem {
    item_id: ItemId,
    unit_price: Amount,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
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

#[derive(SurrealValue)]
struct ItemWrapper {
    id: Option<RecordId>,
    item: SerdeWrapper<Item>,
}

impl From<Item> for ItemWrapper {
    fn from(value: Item) -> Self {
        Self {
            id: None,
            item: SerdeWrapper(value),
        }
    }
}

impl From<ItemWrapper> for Item {
    fn from(value: ItemWrapper) -> Self {
        value.item.0
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

    let r1 = db
        .create::<Option<ItemWrapper>>((ITEM_TABLE, item1.id().as_str()))
        .content(ItemWrapper::from(item1))
        .await?;
    let r2 = db
        .create::<Option<ItemWrapper>>((ITEM_TABLE, item2.id().as_str()))
        .content(ItemWrapper::from(item2))
        .await?;
    let r3 = db
        .create::<Option<ItemWrapper>>((ITEM_TABLE, item3.id().as_str()))
        .content(ItemWrapper::from(item3))
        .await?;

    println!("created: {:?}", r1.map(Item::from));
    println!("created: {:?}", r2.map(Item::from));
    println!("created: {:?}", r3.map(Item::from));

    Ok(())
}

async fn select_data(db: &Surreal<Db>) -> surrealdb::Result<()> {
    let rs1: Vec<surrealdb::types::Value> = db.select(ITEM_TABLE).await?;

    for r in rs1 {
        println!("{:?}", r);
    }

    println!("-----");

    let q =
        "SELECT * FROM items WHERE item.unit_price > 1000";

    let mut rs2 = db.query(q).await?;

    for r in rs2.take::<Vec<ItemWrapper>>(0)? {
        println!("{:?}", Item::from(r));
    }

    Ok(())
}
