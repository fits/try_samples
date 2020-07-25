
use warp::{Filter, Rejection};
use serde::{Deserialize, Serialize};

use std::collections::HashMap;
use std::sync::{Arc, RwLock};

#[derive(Debug, Clone, Deserialize, Serialize)]
struct Item {
    id: String,
    value: i32,
}

#[derive(Debug)]
struct StoreError;

impl warp::reject::Reject for StoreError {}

fn store_err<E: std::fmt::Debug>(e: E) -> Rejection {
    println!("store error: {:?}", e);

    warp::reject::custom(StoreError)
}

type Store = Arc<RwLock<HashMap<String, Item>>>;

#[tokio::main]
async fn main() {
    let store = Store::default();

    let read_store = store.clone();

    let get_item = warp::path!("items" / String)
        .and(warp::any().map(move || read_store.clone()))
        .and_then(|id: String, store: Store| async move {
            println!("*** GET: id={}", id);

            let s = store
                .read()
                .map_err(store_err)?;

            s.get(&id)
                .map(|item|
                    warp::reply::json(&item)
                )
                .ok_or(warp::reject::not_found())
        });

    let write_store = store.clone();

    let update_item = warp::path!("items")
        .and(warp::put())
        .and(warp::body::json::<Item>())
        .and(warp::any().map(move || write_store.clone()))
        .and_then(|item: Item, store: Store| async move {
            println!("*** PUT: {:?}", item);

            let mut s = store
                .write()
                .map_err(store_err)?;

            s.insert(item.id.clone(), item.clone());

            Ok::<_, Rejection>(warp::reply::json(&item))
        });

    let api = get_item.or(update_item);

    warp::serve(api)
        .run(([127, 0, 0, 1], 8080))
        .await;
}
