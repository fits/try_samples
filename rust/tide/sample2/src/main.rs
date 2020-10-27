
use serde::{Deserialize, Serialize};
use tide::{Body, Request, StatusCode, Error};
use uuid::Uuid;

use std::collections::HashMap;
use std::sync::{Arc, RwLock};

#[derive(Default, Clone)]
struct Store(Arc<RwLock<HashMap<String, Item>>>);

#[derive(Debug, Clone, Serialize)]
struct Item {
    id: String,
    name: String,
    value: i32,
}

#[derive(Debug, Clone, Deserialize)]
struct ItemInput {
    name: String,
    value: i32,
}

#[async_std::main]
async fn main() -> tide::Result<()> {
    let store = Store::default();

    let mut app = tide::with_state(store);

    app.at("/items")
        .post(create_item)
        .get(all_items);

    app.at("/items/:id").get(find_item);

    app.listen("127.0.0.1:8080").await?;

    Ok(())
}

fn error(msg: String) -> Error {
    Error::from_str(StatusCode::InternalServerError, msg)
}

async fn create_item(mut req: Request<Store>) -> tide::Result {
    let input: ItemInput = req.body_json().await?;

    let mut store = req.state().0.write().map_err(|e|
        error(format!("write lock error: {:?}", e))
    )?;

    let item = Item {
        id: format!("item-{}", Uuid::new_v4()),
        name: input.name.clone(),
        value: input.value,
    };

    if store.insert(item.id.clone(), item.clone()).is_none() {
        Body::from_json(&item).map(Body::into)
    } else {
        Err(error("duplicate id".to_string()))
    }
}

async fn all_items(req: Request<Store>) -> tide::Result {
    let store = req.state().0.read().map_err(|e|
        error(format!("read lock error: {:?}", e))
    )?;

    let items: Vec<_> = store.values().cloned().collect();

    Body::from_json(&items)
        .map(Body::into)
}

async fn find_item(req: Request<Store>) -> tide::Result {
    let store = req.state().0.read().map_err(|e|
        error(format!("read lock error: {:?}", e))
    )?;

    req.param("id")
        .map(|id|
            store.get(id)
                .ok_or(Error::from_str(StatusCode::NotFound, ""))
        )?
        .map(Body::from_json)?
        .map(Body::into)
}
