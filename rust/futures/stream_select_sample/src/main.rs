
use futures::executor::block_on;
use futures::{stream, FutureExt, StreamExt};

fn stream_select() {
    let f1 = async { Some(1) };
    let f2 = async { Some(2) };

    let s1 = stream::select(f1.into_stream(), f2.into_stream())
        .flat_map(stream::iter)
        .collect::<Vec<_>>();

    let r1 = block_on(s1);

    println!("select-1: {:?}", r1);
    assert_eq!(vec![1, 2], r1);

    let f3 = async { None::<i32> };
    let f4 = async { Some(4) };

    let s2 = stream::select(f3.into_stream(), f4.into_stream())
        .flat_map(stream::iter)
        .collect::<Vec<_>>();

    let r2 = block_on(s2);

    println!("select-2: {:?}", r2);
    assert_eq!(vec![4], r2);
}

fn chain() {
    let f1 = async { Some(1) };
    let f2 = async { Some(2) };

    let s1 = f1.into_stream()
        .chain(f2.into_stream())
        .flat_map(stream::iter)
        .collect::<Vec<_>>();

    let r1 = block_on(s1);

    println!("chain-1: {:?}", r1);
    assert_eq!(vec![1, 2], r1);

    let f3 = async { None::<i32> };
    let f4 = async { Some(4) };

    let s2 = f3.into_stream()
        .chain(f4.into_stream())
        .flat_map(stream::iter)
        .collect::<Vec<_>>();

    let r2 = block_on(s2);

    println!("chain-2: {:?}", r2);
    assert_eq!(vec![4], r2);
}

fn main() {
    stream_select();
    chain();
}