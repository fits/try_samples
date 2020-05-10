
use futures::{future, executor};
use futures::stream::{self, StreamExt};

fn sample1() {
    let stream = stream::once(async { 12 });
    
    let res = async {
        let r = stream.collect::<Vec<i32>>().await;
        println!("sample1: {:?}", r);
    };

    executor::block_on(res);
}

fn sample2() {
    let s1 = stream::once(async { 12 });
    let s2 = stream::iter(vec![34, 56]);

    let s = s1.chain(s2);

    let f = s.collect::<Vec<_>>();
    println!("sample2: {:?}", executor::block_on(f));
}

fn sample3() {
    let s1 = stream::once(async { 12 });
    let s2 = stream::iter(vec![34, 56]);

    let s = s1.chain(s2);

    let f = s.for_each(|v| {
        println!("sample3: {}", v);
        future::ready(())
    });

    executor::block_on(f);
}

fn sample4() {
    let s = stream::iter(vec![12, 34, 56]);

    for r in executor::block_on_stream(s) {
        println!("sample4: {}", r);
    }
}

fn sample5() {
    let s1 = stream::once(async { 12 });
    let s2 = stream::iter(vec![34, 56]);

    let s = s1.chain(s2);

    for r in executor::block_on_stream(s.boxed_local()) {
        println!("sample5: {}", r);
    }
}

fn main() {
    sample1();
    sample2();
    sample3();
    sample4();
    sample5();
}