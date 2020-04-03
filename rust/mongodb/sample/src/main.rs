
use mongodb::{Client, options::ClientOptions, error::Result as MongoResult};
use bson::doc;

fn run_sample(uri: &str, db_name: &str, col_name: &str) -> MongoResult<()> {
    let opt = ClientOptions::parse(uri)?;
    let client = Client::with_options(opt)?;

    let db = client.database(db_name);
    let col = db.collection(col_name);

    let res = col.insert_one(doc! {"value": 10}, None)?;

    println!("insert id: {:?}", res.inserted_id);

    let cursor = col.find(None, None)?;

    for r in cursor {
        match r {
            Ok(d) => println!("_id: {:?}, value: {:?}", d.get("_id"), d.get("value")),
            Err(e) => println!("error: {:?}", e),
        }
    }

    Ok(())
}

fn main() {
    let uri = "mongodb://localhost";
    let db_name = "sample";
    let col_name = "data";

    run_sample(uri, db_name, col_name).unwrap();
}
