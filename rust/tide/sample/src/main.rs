
use serde::Serialize;
use tide::{Body, Request};

#[derive(Debug, Serialize)]
struct Item {
    name: String,
    value: i32,
}

#[async_std::main]
async fn main() -> tide::Result<()> {
    let mut app = tide::new();

    app.at("/items").get(find_items);

    app.listen("127.0.0.1:8080").await?;

    Ok(())
}

async fn find_items(_: Request<()>) -> tide::Result {
    let res = vec![
        Item { name: "item-1".into(), value: 1 },
        Item { name: "item-2".into(), value: 2 },
        Item { name: "item-3".into(), value: 3 },
    ];

    Body::from_json(&res)
        .map(Body::into)
}
