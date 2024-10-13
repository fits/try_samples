use async_std::task;
use futures::executor::block_on;
use futures::future::join_all;
use rand::Rng;

use std::time::Duration;

fn main() {
    let ts = (1..5).map(|x| task(x));

    let f = join_all(ts);

    block_on(f);
}

async fn task(n: u32) {
    let t = 1 + (rand::thread_rng().gen::<f32>() * 10.0) as u64;

    println!("start: id={}", n);

    task::sleep(Duration::from_secs(t)).await;

    println!("done: id={}, wait={}s", n, t);
}
