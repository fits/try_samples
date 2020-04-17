
use std::env;
use actix_web::{get, post, error, web, HttpServer, App, 
                HttpResponse, Result as HttpResult};
use mongodb::{Client, Collection, error::Result as MongoResult};
use serde::{Deserialize, Serialize};
use bson::{Bson, oid::ObjectId};

#[derive(Clone)]
struct Store {
    client: Client,
    db_name: String,
    col_name: String,
}

impl Store {
    fn collection(&self) -> Collection {
        self.client.database(&self.db_name)
                    .collection(&self.col_name)
    }
}

#[derive(Debug, Clone, Deserialize, Serialize)]
struct CreateItem {
    name: String,
    value: i32,
}

#[post("/items")]
async fn create_item(db: web::Data<Store>, 
    params: web::Json<CreateItem>) -> HttpResult<HttpResponse> {

    let doc = bson::to_bson(&params.0)
                    .map_err(response_error)?;

    if let Bson::Document(d) = doc {
        let col = db.collection();

        let res = col.insert_one(d, None)
                        .map_err(response_error)?;

        let id: ObjectId = bson::from_bson(res.inserted_id)
                                    .map_err(response_error)?;

        Ok(HttpResponse::Created().json(id.to_hex()))
    } else {
        Err(response_error(""))
    }
}

#[get("/items")]
async fn all_items(db: web::Data<Store>) -> HttpResult<HttpResponse> {
    let col = db.collection();

    let rs = col.find(None, None)
                .and_then(|c|
                    c.collect::<MongoResult<Vec<_>>>()
                )
                .map_err(response_error)?;

    Ok(HttpResponse::Ok().json(rs))
}

fn response_error<E>(err: E) -> error::Error
    where
        E: std::fmt::Debug + std::fmt::Display + 'static
{
    println!("ERROR: {:?}", err);
    error::ErrorInternalServerError(err)
}

#[actix_rt::main]
async fn main() -> std::io::Result<()> {
    let addr = "127.0.0.1:8080";

    let mongo_uri = env::var("MONGO_URI").unwrap_or("mongodb://localhost".to_string());
    let db = env::var("MONGO_DB").unwrap_or("items".to_string());
    let col = env::var("MONGO_COLLECTION").unwrap_or("data".to_string());

    let store = Store {
        client: Client::with_uri_str(&mongo_uri).unwrap(),
        db_name: db,
        col_name: col
    };

    HttpServer::new( move || 
        App::new()
            .data(store.clone())
            .service(create_item)
            .service(all_items)
    ).bind(addr)?.run().await
}
