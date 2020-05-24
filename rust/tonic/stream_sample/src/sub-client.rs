
use tonic::Request;
use std::env;

pub mod pubsub {
    tonic::include_proto!("pubsub");
}

use pubsub::message_service_client::MessageServiceClient;
use pubsub::SubscribeRequest;

#[tokio::main]
async fn main() {
    let client_id = env::args().nth(1).unwrap();

    println!("run subscribe client");

    let url = "http://localhost:50051";

    let mut client = MessageServiceClient::connect(url).await.unwrap();

    let req = SubscribeRequest { client_id };

    let res = client.subscribe(Request::new(req)).await.unwrap();

    println!("res: {:?}", res);

    let mut inbound = res.into_inner();

    loop {
        let msg = inbound.message().await;
        println!("msg: {:?}", msg);

        if msg.is_err() {
            break;
        }
    }
}