
use std::io;
use futures::prelude::*;
use futures::future::{self, Ready};
use tarpc::{context, client, server, server::Handler, transport};
use serde::{Deserialize, Serialize};

type DataId = String;

#[derive(Debug, Deserialize, Serialize)]
struct Data {
    id: DataId,
    value: i32,
}

#[tarpc::service]
trait Sample {
    async fn send(data: Data) -> Option<DataId>;
}

#[derive(Clone)]
struct SampleServer;

impl Sample for SampleServer {
    type SendFut = Ready<Option<DataId>>;

    fn send(self, _: context::Context, data: Data) -> Self::SendFut {
        println!("*** server received: {:?}", data);

        if data.value % 2 == 0 {
            future::ready(Some(data.id))
        } else {
            future::ready(None)
        }
    }
}

#[tokio::main]
async fn main() -> io::Result<()> {
    let (cl_ch, sv_ch) = transport::channel::unbounded();

    let server = server::new(server::Config::default())
                        .incoming(stream::once(future::ready(sv_ch)))
                        .respond_with(SampleServer.serve());

    tokio::spawn(server);

    let mut client = SampleClient::new(
        client::Config::default(), 
        cl_ch
    ).spawn()?;

    let d1 = Data { id: "id1".to_string(), value: 1 };
    let d2 = Data { id: "id2".to_string(), value: 2 };

    let r1 = client.send(context::current(), d1).await?;
    println!("client result: {:?}", r1);

    let r2 = client.send(context::current(), d2).await?;
    println!("client result: {:?}", r2);

    Ok(())
}