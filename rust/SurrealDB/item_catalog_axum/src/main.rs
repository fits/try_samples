use axum::extract::Path;
use axum::http::StatusCode;
use axum::routing::{get, post};
use axum::{Extension, Json, Router};

use serde::{Deserialize, Serialize};

use surrealdb::Surreal;
use surrealdb::engine::local::{Db, SurrealKv};
use surrealdb::types::SurrealValue;

use std::sync::Arc;

const ITEMS_TABLE: &str = "items";

type ItemCode = String;
type Amount = i64;

#[derive(Debug, Clone, SurrealValue, Serialize, Deserialize)]
struct Item {
    code: ItemCode,
    unit_price: Amount,
}

struct Context(Surreal<Db>);

type AppError = Box<dyn std::error::Error>;

#[tokio::main]
async fn main() -> Result<(), AppError> {
    let db = Surreal::new::<SurrealKv>("./db").await?;
    db.use_ns("example").use_db("catalog").await?;

    let ctx = Arc::new(Context(db));

    let app = Router::new()
        .route("/items", post(create_item))
        .route("/items/{code}", get(find_item))
        .layer(Extension(ctx));

    let listener = tokio::net::TcpListener::bind("127.0.0.1:8080").await?;

    axum::serve(listener, app).await?;

    Ok(())
}

async fn create_item(
    Extension(ctx): Extension<Arc<Context>>,
    Json(input): Json<Item>,
) -> Result<Json<Item>, StatusCode> {
    if input.code.trim().is_empty() || input.unit_price < 0 {
        Err(StatusCode::BAD_REQUEST)
    } else {
        let id = (ITEMS_TABLE, input.code.as_str());

        let res: Option<Item> = ctx.0.create(id).content(input).await.map_err(|e| {
            println!("create error: {}", e);

            if e.is_already_exists() {
                StatusCode::BAD_REQUEST
            } else {
                StatusCode::INTERNAL_SERVER_ERROR
            }
        })?;

        res.map(Json).ok_or(StatusCode::INTERNAL_SERVER_ERROR)
    }
}

async fn find_item(
    Path(code): Path<ItemCode>,
    Extension(ctx): Extension<Arc<Context>>,
) -> Result<Json<Item>, StatusCode> {
    let id = (ITEMS_TABLE, code);

    let res: Option<Item> = ctx.0.select(id).await.map_err(|e| {
        println!("select error: {}", e);
        StatusCode::INTERNAL_SERVER_ERROR
    })?;

    res.map(Json).ok_or(StatusCode::NOT_FOUND)
}
