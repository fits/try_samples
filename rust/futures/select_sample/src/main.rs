
use futures::{executor, pin_mut, select};
use futures::future::{self, FutureExt};
use futures::stream::{self, StreamExt};

async fn value1() -> u8 {
    11
}

async fn sample1() {
    let s1 = value1().fuse();
    let s2 = async { 22 }.fuse();

    pin_mut!(s1, s2);

    let mut s3 = Box::pin(async { 33 }.fuse());

    select! {
        x = s1 => println!("sample1 s1: {:?}", x),
        x = s2 => println!("sample1 s2: {:?}", x),
        x = s3 => println!("sample1 s3: {:?}", x),
    };
}

async fn sample2() {
    let mut s1 = future::ready(11);
    let mut s2 = future::ready(22);

    select! {
        x = s1 => println!("sample2 s1: {:?}", x),
        x = s2 => println!("sample2 s2: {:?}", x),
    };
}

async fn sample3() {
    let mut s1 = stream::iter(0..10).fuse();
    let mut s2 = stream::iter(20..30).fuse();

    for _ in 0..10 {
        select! {
            x = s1.next() => println!("sample3 s1: {:?}", x),
            x = s2.next() => println!("sample3 s2: {:?}", x),
        };
    }
}

async fn sample4() {
    let mut s1 = stream::iter(10..15).fuse();
    let mut s2 = stream::iter(25..30).fuse();

    loop {
        select! {
            x = s1.next() => println!("sample4 s1: {:?}", x),
            x = s2.next() => println!("sample4 s2: {:?}", x),
            complete => {
                println!("sample4 complete");
                break
            },
        };
    }
}

fn main() {
    executor::block_on(sample1());
    executor::block_on(sample2());
    executor::block_on(sample3());
    executor::block_on(sample4());
}