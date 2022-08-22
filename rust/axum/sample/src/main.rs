
use axum::{extract::Query, routing::get, Router};
use serde::Deserialize;
use std::net::SocketAddr;

#[derive(Debug, Deserialize)]
struct Param {
    keyword: Option<String>,
    size: Option<usize>,
}

#[tokio::main]
async fn main() {
    let app = Router::new()
        .route("/", get(handler))
        .route("/sample", get(params_handler));


    let addr = SocketAddr::from(([127, 0, 0, 1], 8080));

    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .unwrap();
}

async fn handler() -> &'static str {
    "ok"
}

async fn params_handler(Query(param): Query<Param>) -> String {
    println!("{:?}", param);
    format!("ok:{}, {}", param.keyword.unwrap_or("".to_string()), param.size.unwrap_or(0))
}
