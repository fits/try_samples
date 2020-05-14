
use futures::executor::LocalPool;
use futures::task::LocalSpawnExt;
use futures::future::lazy;
use std::thread;

fn main() {
    let mut pool = LocalPool::new();

    let res = pool.run_until(async { 11 });

    println!("run_until: {:?}, thread_id = {:?}", res, thread::current().id());

    let spawner = pool.spawner();

    for i in 0..5 {
        spawner.spawn_local(async move {
            let th_id = thread::current().id();
            println!("spawn_local: {}, thread_id = {:?}", i, th_id);
        }).unwrap();
    }

    pool.try_run_one();
    pool.try_run_one();

    println!("---");

    pool.run();

    println!("---");

    let res2 = pool.run_until(lazy(|_| 22));

    println!("res2 = {}", res2);
}