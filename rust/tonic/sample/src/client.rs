
use tonic::Request;

pub mod item {
    tonic::include_proto!("item");
}

use item::item_service_client::ItemServiceClient;
use item::ItemRequest;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    println!("run client");

    let url = "http://localhost:50051";

    let mut client = ItemServiceClient::connect(url).await?;

    let msg = ItemRequest {
        item_id: "id1".to_string(),
    };

    let res = client.get_item(Request::new(msg)).await?;

    println!("*** res: {:?}", res);

    let item = res.into_inner();

    println!("*** item: id={}, price={}", item.item_id, item.price);

    Ok(())
}