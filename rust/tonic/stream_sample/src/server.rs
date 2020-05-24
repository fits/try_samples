
use tonic::{transport::Server, Request, Response, Status};
use tokio::sync::mpsc;
use std::collections::HashMap;
use std::sync::{Arc, RwLock};

pub mod pubsub {
    tonic::include_proto!("pubsub");
}

use pubsub::message_service_server::{MessageService, MessageServiceServer};
use pubsub::{Message, SubscribeRequest};

type Subscribes = HashMap<String, mpsc::Sender<Result<Message, Status>>>;

#[derive(Debug, Default)]
struct SampleMessageService {
    subscribes: Arc<RwLock<Subscribes>>,
}

type RpcResult<T> = Result<Response<T>, Status>;

#[tonic::async_trait]
impl MessageService for SampleMessageService {
    type SubscribeStream = mpsc::Receiver<Result<Message, Status>>;

    async fn publish(&self, req: Request<Message>) -> RpcResult<()> {
        println!("*** publish: {:?}", req);

        let msg = req.into_inner();
        let rs = self.subscribes.read().unwrap().clone();

        for (k, v) in rs.iter() {
            let r = v.clone().send(Ok(msg.clone())).await;

            println!("{:?}", r);

            if r.is_err() {
                println!("remove client_id: {}", k);
                
                let mut ws = self.subscribes.write().unwrap();
                ws.remove(k);
            }
        }

        Ok(Response::new(()))
    }

    async fn subscribe(&self, req: Request<SubscribeRequest>) -> RpcResult<Self::SubscribeStream> {
        println!("*** subscribe: {:?}", req);

        let (tx, rx) = mpsc::channel(4);

        let mut ws = self.subscribes.write().unwrap();
        let sr = req.into_inner();

        if ws.contains_key(&sr.client_id) {
            println!("*** exists client_id: {}", sr.client_id);

            let msg = format!("exists client_id={}", sr.client_id);
            return Err(Status::already_exists(msg))
        }

        ws.insert(sr.client_id, tx);

        Ok(Response::new(rx))
    }
}

#[tokio::main]
async fn main() {
    println!("run server");

    let addr = "127.0.0.1:50051".parse().unwrap();
    let svc = SampleMessageService::default();

    Server::builder()
        .add_service(MessageServiceServer::new(svc))
        .serve(addr)
        .await
        .unwrap();
}
