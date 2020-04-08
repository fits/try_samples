
use actix_web::{get, post, web, App, HttpServer, HttpResponse};
use serde::{Deserialize, Serialize};

use std::collections::HashMap;
use std::sync::atomic::{AtomicU32, Ordering};
use std::sync::Mutex;

type StockMapData = web::Data<Mutex<HashMap<u32, Stock>>>;

#[derive(Debug, Deserialize)]
struct CreateStock {
    name: String,
    qty: u32,
}

#[derive(Debug, Clone, Serialize)]
struct Stock {
    id: u32,
    name: String,
    qty: u32,
}

#[get("/stocks")]
async fn find_all_stocks(data: StockMapData) -> HttpResponse {
    let store = data.lock().unwrap();
    let res: Vec<_> = store.values().collect();

    HttpResponse::Ok().json(res)
}

#[get("/stocks/{id}")]
async fn find_stock(data: StockMapData, info: web::Path<(u32,)>) -> HttpResponse {
    let store = data.lock().unwrap();

    match store.get(&info.0) {
        Some(r) => HttpResponse::Ok().json(r),
        None => HttpResponse::NotFound().finish(),
    }
}

#[post("/stocks")]
async fn create_stock(counter: web::Data<AtomicU32>, 
    data: StockMapData, params: web::Json<CreateStock>) -> HttpResponse {

    counter.fetch_add(1, Ordering::SeqCst);
    let id = counter.load(Ordering::SeqCst);

    let res = Stock { id: id, name: params.0.name, qty: params.0.qty };

    println!("new stock : {:?}", res);

    let mut store = data.lock().unwrap();
    store.insert(id, res.clone());

    HttpResponse::Created().json(res)
}

#[actix_rt::main]
async fn main() -> std::io::Result<()> {
    let addr = "127.0.0.1:8080";

    let store: StockMapData = web::Data::new(Mutex::new(HashMap::new()));
    let idcounter = web::Data::new(AtomicU32::new(0));

    HttpServer::new( move || {
        App::new()
            .app_data(store.clone())
            .app_data(idcounter.clone())
            .service(create_stock)
            .service(find_stock)
            .service(find_all_stocks)
    }).bind(addr)?.run().await
}
