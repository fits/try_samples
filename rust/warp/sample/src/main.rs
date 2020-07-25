
use warp::Filter;
use serde::{Deserialize, Serialize};

#[derive(Debug, Deserialize, Serialize)]
struct Item {
    id: String,
    value: i32,
}

#[tokio::main]
async fn main() {
    let get_item = warp::path!("items" / String)
        .map(|id| 
            warp::reply::json(
                &Item { id, value: 12 }
            )
        );

    let post_item = warp::path!("items")
        .and(warp::post())
        .and(warp::body::json::<Item>())
        .map(|item| {
            println!("*** POST: {:?}", item);
            warp::http::StatusCode::CREATED
        });

    let api = get_item.or(post_item);

    warp::serve(api)
        .run(([127, 0, 0, 1], 8080))
        .await;
}
