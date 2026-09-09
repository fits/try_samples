
use axum::{extract::Query, routing::get, Router};
use serde::Deserialize;

#[derive(Debug, Deserialize)]
struct Param {
    keyword: Option<String>,
    size: Option<usize>,
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    let app = Router::new()
        .route("/", get(handler))
        .route("/sample", get(params_handler));

    let listener = tokio::net::TcpListener::bind("127.0.0.1:8080").await?;

    axum::serve(listener, app).await?;

    Ok(())
}

async fn handler() -> &'static str {
    "ok"
}

async fn params_handler(Query(param): Query<Param>) -> String {
    println!("{:?}", param);
    format!("ok:{}, {}", param.keyword.unwrap_or_default(), param.size.unwrap_or_default())
}
