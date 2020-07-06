
use futures::executor::block_on;
use futures::{future, FutureExt};

fn main() {
    let f1 = async { Some(1) };
    let f2 = async { Some(2) };

    let j1 = future::join(f1, f2)
        .map(|(a, b)| 
            a.iter().chain(b.iter()).cloned().collect::<Vec<_>>()
        );

    let r1 = block_on(j1);

    println!("{:?}", r1);
    assert_eq!(vec![1, 2], r1);

    let f3 = async { None::<i32> };
    let f4 = async { Some(4) };

    let j2 = future::join(f3, f4)
        .map(|(a, b)| 
            a.iter().chain(b.iter()).cloned().collect::<Vec<_>>()
        );

    let r2 = block_on(j2);

    println!("{:?}", r2);
    assert_eq!(vec![4], r2);
}