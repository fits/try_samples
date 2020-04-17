
use std::env;
use actix_web::{get, post, error, web, HttpServer, App, HttpResponse};
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

#[derive(Debug)]
struct AppError(Box<dyn std::error::Error>);

impl std::fmt::Display for AppError {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> Result<(), std::fmt::Error> {
        std::fmt::Display::fmt(&self.0, f)
    }
}

impl<E> From<E> for AppError 
    where
        E: std::error::Error + 'static
{
    fn from(err: E) -> Self {
        Self(Box::new(err))
    }
}

impl error::ResponseError for AppError {}

#[derive(Debug, Clone, Deserialize, Serialize)]
struct CreateItem {
    name: String,
    value: i32,
}

type AppResult = Result<HttpResponse, AppError>;

#[post("/items")]
async fn create_item(db: web::Data<Store>, 
    params: web::Json<CreateItem>) -> AppResult {

    let doc = bson::to_bson(&params.0)?;

    if let Bson::Document(d) = doc {
        let col = db.collection();

        let res = col.insert_one(d, None)?;

        let id: ObjectId = bson::from_bson(res.inserted_id)?;

        Ok(HttpResponse::Created().json(id.to_hex()))
    } else {
        Err(error::ErrorInternalServerError("").into())
    }
}

#[get("/items")]
async fn all_items(db: web::Data<Store>) -> AppResult {
    let col = db.collection();

    let rs = col.find(None, None)
                .and_then(|c|
                    c.collect::<MongoResult<Vec<_>>>()
                )?;

    Ok(HttpResponse::Ok().json(rs))
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
