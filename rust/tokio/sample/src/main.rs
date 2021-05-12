
use std::error::Error;

async fn sample() -> i32 {
    println!("called sample");
    123
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let r = sample().await;

    println!("{}", r);

    Ok(())
}
