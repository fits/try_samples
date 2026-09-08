use surrealdb::Surreal;
use surrealdb::engine::local::{Db, Mem};
use surrealdb::types::{Kind, Object, SurrealValue, Value};

const ITEM_TABLE: &str = "items";

type ItemId = String;
type Amount = u64;

#[derive(Debug, Clone)]
enum Item {
    Single(SingleItem),
    Bundle(BundleItem),
}

#[derive(Debug, Clone)]
struct SingleItem {
    item_id: ItemId,
    unit_price: Amount,
}

#[derive(Debug, Clone)]
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

impl SurrealValue for Item {
    fn kind_of() -> Kind {
        Kind::Any
    }

    fn into_value(self) -> Value {
        match self {
            Self::Single(x) => {
                let res = x.into_value();

                if let Value::Object(mut x) = res {
                    x.insert("type", "single");
                    x.into()
                } else {
                    res
                }
            }
            Self::Bundle(x) => {
                let res = x.into_value();

                if let Value::Object(mut x) = res {
                    x.insert("type", "bundle");
                    x.into()
                } else {
                    res
                }
            }
        }
    }

    fn from_value(value: Value) -> Result<Self, surrealdb::Error>
    where
        Self: Sized,
    {
        if let Value::Object(x) = value {
            let ty = x
                .get("type")
                .ok_or(surrealdb::Error::not_found("type".into(), None))?;

            match ty {
                Value::String(t) if t == "single" => {
                    let v = SingleItem::from_value(x.into())?;
                    Ok(Self::Single(v))
                }
                _ => {
                    let v = BundleItem::from_value(x.into())?;
                    Ok(Self::Bundle(v))
                }
            }
        } else {
            Err(surrealdb::Error::internal("no object".into()))
        }
    }
}

impl SurrealValue for SingleItem {
    fn kind_of() -> Kind {
        Kind::Object
    }

    fn into_value(self) -> Value {
        let mut res = Object::new();

        res.insert("item_id", self.item_id);
        res.insert("unit_price", self.unit_price);

        res.into()
    }

    fn from_value(value: Value) -> Result<Self, surrealdb::Error>
    where
        Self: Sized,
    {
        if let Value::Object(x) = value {
            let item_id = x
                .get("item_id")
                .ok_or(surrealdb::Error::not_found("item_id".into(), None))?;

            let unit_price = x
                .get("unit_price")
                .ok_or(surrealdb::Error::not_found("unit_price".into(), None))?;

            Ok(Self {
                item_id: ItemId::from_value(item_id.to_owned())?,
                unit_price: Amount::from_value(unit_price.to_owned())?,
            })
        } else {
            Err(surrealdb::Error::internal("invalid value".into()))
        }
    }
}

impl SurrealValue for BundleItem {
    fn kind_of() -> Kind {
        Kind::Object
    }

    fn into_value(self) -> Value {
        let mut res = Object::new();

        res.insert("bundle_id", self.bundle_id);
        res.insert("unit_price", self.unit_price);

        let details = self
            .details
            .iter()
            .map(|x| x.clone().into_value())
            .collect::<Vec<_>>();

        res.insert("details", details);

        res.into()
    }

    fn from_value(value: Value) -> Result<Self, surrealdb::Error>
    where
        Self: Sized,
    {
        if let Value::Object(x) = value {
            let bundle_id = x
                .get("bundle_id")
                .ok_or(surrealdb::Error::not_found("bundle_id".into(), None))?;

            let unit_price = x
                .get("unit_price")
                .ok_or(surrealdb::Error::not_found("unit_price".into(), None))?;

            let details = x
                .get("details")
                .ok_or(surrealdb::Error::not_found("details".into(), None))?;

            Ok(Self {
                bundle_id: ItemId::from_value(bundle_id.to_owned())?,
                unit_price: Amount::from_value(unit_price.to_owned())?,
                details: Vec::from_value(details.to_owned())?,
            })
        } else {
            Err(surrealdb::Error::internal("invalid value".into()))
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

    let r1 = db
        .create::<Option<Item>>((ITEM_TABLE, item1.id().as_str()))
        .content(item1)
        .await?;
    let r2 = db
        .create::<Option<Item>>((ITEM_TABLE, item2.id().as_str()))
        .content(item2)
        .await?;
    let r3 = db
        .create::<Option<Item>>((ITEM_TABLE, item3.id().as_str()))
        .content(item3)
        .await?;

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

    let q = "SELECT * FROM items WHERE unit_price > 1000";

    let mut rs2 = db.query(q).await?;

    for r in rs2.take::<Vec<Item>>(0)? {
        println!("{:?}", r);
    }

    Ok(())
}
