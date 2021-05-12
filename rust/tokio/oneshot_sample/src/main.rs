
use std::error::Error;
use tokio::sync::oneshot;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let (tx, rx) = oneshot::channel();

    tokio::spawn(async move {
        if let Err(_) = tx.send("abc") {
            println!("failed send");
        }
    });

    let r = rx.await?;

    println!("received = {}", r);

    Ok(())
}
