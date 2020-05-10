
use futures::channel::mpsc;
use futures::executor;
use std::thread;

fn sample() {
    let n = 50;

    let pool = executor::ThreadPool::new().unwrap();

    let (tx, rx) = mpsc::unbounded::<i32>();

    for i in 0..n {
        let tx = tx.clone();

        let f = async move {
            println!("send: {}, thread id: {:?}", i, thread::current().id());
            tx.unbounded_send(i).expect("send error");
        };

        pool.spawn_ok(f);
    }

    for r in executor::block_on_stream(rx) {
        println!("recv: {}", r);
    }
}

fn main() {
    sample();
}