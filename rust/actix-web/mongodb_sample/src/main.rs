
use actix_web::{get, post, web, error, App, HttpServer, HttpResponse};
use serde::Deserialize;

use mongodb::Client;
use bson::doc;

#[derive(Debug, Deserialize)]
struct CreateStock {
    name: String,
    qty: u32,
}

#[get("/stocks")]
async fn find_all_stocks(db: web::Data<Client>) -> HttpResponse {
    let col = db.database("stocks").collection("data");

    let res = col.find(None, None)
                    .and_then(|rs| rs.collect::<mongodb::error::Result<Vec<_>>>());

    match res {
        Ok(r) => HttpResponse::Ok().json(r),
        Err(e) => {
            println!("error: {:?}", e);
            error::ErrorInternalServerError(e).into()
        },
    }
}

#[get("/stocks/{id}")]
async fn find_stock(db: web::Data<Client>, info: web::Path<(String,)>) -> HttpResponse {

    let col = db.database("stocks").collection("data");

    let id = bson::oid::ObjectId::with_string(&info.0)
                                .map_err(|e| {
                                    println!("error: {:?}", e);
                                    //HttpResponse::BadRequest().finish()
                                    error::ErrorBadRequest(e).into()
                                });

    let res = id.and_then(|id| 
        col.find_one(Some(doc! {"_id": id}), None)
            .map_err(|e| {
                println!("error: {:?}", e);
                //HttpResponse::InternalServerError().finish()
                error::ErrorInternalServerError(e).into()
            })
    );

    match res {
        Ok(r) => HttpResponse::Ok().json(r),
        Err(e) => e,
    }
}

#[post("/stocks")]
async fn create_stock(db: web::Data<Client>, params: web::Json<CreateStock>) -> HttpResponse {

    let col = db.database("stocks").collection("data");

    let doc = doc! {"name": params.0.name, "value": params.0.qty };
    let res = col.insert_one(doc, None);

    match res {
        Ok(r) => {
            println!("{:?}", r.inserted_id);

            let oid = bson::from_bson::<bson::oid::ObjectId>(r.inserted_id).unwrap();
            HttpResponse::Created().json(oid.to_hex())
        },
        Err(e) => {
            println!("error: {:?}", e);
            //HttpResponse::InternalServerError().finish()
            error::ErrorInternalServerError(e).into()
        }
    }
}

#[actix_rt::main]
async fn main() -> std::io::Result<()> {
    let addr = "127.0.0.1:8080";
    let mongo = Client::with_uri_str("mongodb://localhost").unwrap();

    HttpServer::new( move || {
        App::new()
            .data(mongo.clone())
            .service(create_stock)
            .service(find_stock)
            .service(find_all_stocks)
    }).bind(addr)?.run().await
}
