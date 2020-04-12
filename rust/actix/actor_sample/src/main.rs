
use actix::prelude::*;

#[derive(Debug, Message)]
#[rtype(result = "Data")]
struct CreateData {
    value: u32,
}

#[derive(Debug, MessageResponse)]
struct Data {
    id: String,
    value: u32,
}

struct SampleActor;

impl Actor for SampleActor {
    type Context = Context<Self>;

    fn started(&mut self, _ctx: &mut Self::Context) {
        println!("*** started: {:?}", std::thread::current().id());
    }
}

impl Handler<CreateData> for SampleActor {
    type Result = Data;

    fn handle(&mut self, msg: CreateData, 
        _ctx: &mut Self::Context) -> Self::Result {

        let id = format!("test-{:?}", std::thread::current().id());

        println!("*** {} handle message: {:?}", id, msg);

        Data { id: id, value: msg.value }
    }
}

#[actix_rt::main]
async fn main() {
    let addr = SampleActor.start();

    let a1 = addr.send(CreateData { value: 11}).await;
    println!("a1: {:?}", a1);

    let a2 = addr.send(CreateData { value: 12}).await;
    println!("a2: {:?}", a2);

    let (b1, b2, b3) = futures::join!(
        addr.send(CreateData { value: 21}),
        addr.send(CreateData { value: 22}),
        addr.send(CreateData { value: 23}),
    );

    println!("b1: {:?}", b1);
    println!("b2: {:?}", b2);
    println!("b3: {:?}", b3);

    let rs: Vec<_> = (0..3).map(|i| 
        addr.send(CreateData { value: i + 100 })
    ).collect();

    for r in rs {
        match r.await {
            Ok(r) => println!("ok: {:?}", r),
            Err(e) => println!("err: {}", e),
        }
    }
}
