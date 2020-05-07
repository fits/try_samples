
use futures::executor;
use futures::stream::{self, StreamExt};

fn main() {
    let stream = stream::once(async { 12 });
    
    let res = async {
        let r = stream.collect::<Vec<i32>>().await;
        println!("{:?}", r);
    };

    executor::block_on(res);
}