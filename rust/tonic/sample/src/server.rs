
use tonic::{transport::Server, Request, Response, Status};

pub mod item {
    tonic::include_proto!("item");
}

use item::item_service_server::{ItemService, ItemServiceServer};
use item::{Item, ItemRequest};

#[derive(Debug, Default)]
struct SampleItemService {}

#[tonic::async_trait]
impl ItemService for SampleItemService {
    async fn get_item(&self, req: Request<ItemRequest>) -> Result<Response<Item>, Status> {

        println!("*** req: {:?}", req);

        let res = Item {
            item_id: req.into_inner().item_id,
            price: 100,
        };

        Ok(Response::new(res))
    }
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    println!("run server");

    let addr = "127.0.0.1:50051".parse()?;
    let svc = SampleItemService::default();

    Server::builder()
        .add_service(ItemServiceServer::new(svc))
        .serve(addr)
        .await?;

    Ok(())
}