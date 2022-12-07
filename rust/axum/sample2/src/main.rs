
use axum::{Router, routing::get, response::Json};
use serde_json::{json, Value};
use std::net::SocketAddr;

#[tokio::main]
async fn main() {
    let addr: SocketAddr = "127.0.0.1:8080".parse().unwrap();

    let app = Router::new().route("/", get(handler));

    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .unwrap();
}

async fn handler() -> Json<Value> {
    Json(json!({ "name": "item-1", "value": 12 }))
}