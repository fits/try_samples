
use tonic::Request;
use std::env;

pub mod pubsub {
    tonic::include_proto!("pubsub");
}

use pubsub::message_service_client::MessageServiceClient;
use pubsub::Message;

#[tokio::main]
async fn main() {
    let subject = env::args().nth(1).unwrap_or("nothing".to_string());
    let body = env::args().nth(2).unwrap_or("nothing".to_string());

    println!("run publish client");

    let url = "http://localhost:50051";

    let mut client = MessageServiceClient::connect(url).await.unwrap();

    let msg = Message {
        subject,
        body,
    };

    let res = client.publish(Request::new(msg)).await.unwrap();

    println!("res: {:?}", res);
}