
use async_std::task;

async fn calc(a: i32, b: i32) -> i32 {
    (a + 1) * (a - 1) * b
}

fn main() {
    let r = task::block_on(calc(5, 7));
    println!("result: {}", r);
}
