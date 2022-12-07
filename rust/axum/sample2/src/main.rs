
use axum::{
    Router, 
    extract::{Path, State},
    http::StatusCode,
    routing::{get, post}, 
    response::{Json, IntoResponse}, 
};
use serde::{Deserialize, Serialize};
use serde_json::{json, Value};
use uuid::Uuid;

use std::collections::HashMap;
use std::net::SocketAddr;
use std::sync::{Arc, RwLock};

#[derive(Debug, Clone, Serialize)]
struct Item {
    id: Uuid,
    value: i32,
}

#[derive(Debug, Clone, Deserialize)]
struct CreateItem {
    value: i32,
}

#[derive(Default, Clone)]
struct Store(Arc<RwLock<HashMap<Uuid, Item>>>);

#[tokio::main]
async fn main() {
    let addr: SocketAddr = "127.0.0.1:8080".parse().unwrap();

    let store = Store::default();

    let app = Router::new()
        .route("/", get(home_handler))
        .route("/items", post(item_create))
        .route("/items/:id", get(item_get))
        .with_state(store);

    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .unwrap();
}

async fn home_handler() -> Json<Value> {
    Json(json!({ "description": "json sample" }))
}

async fn item_create(State(store): State<Store>, Json(input): Json<CreateItem>) -> Result<impl IntoResponse, StatusCode> {
    let item = Item {
        id: Uuid::new_v4(),
        value: input.value,
    };

    let mut s = store.0.write().map_err(|_| StatusCode::INTERNAL_SERVER_ERROR)?;

    s.insert(item.id, item.clone());
    
    Ok((StatusCode::CREATED, Json(item)))
}

async fn item_get(Path(id): Path<Uuid>, State(store): State<Store>) -> Result<impl IntoResponse, StatusCode> {
    let s = store.0.read().map_err(|_| StatusCode::INTERNAL_SERVER_ERROR)?;

    if let Some(d) = s.get(&id) {
        Ok(Json(d.clone()))
    } else {
        Err(StatusCode::NOT_FOUND)
    }
}
