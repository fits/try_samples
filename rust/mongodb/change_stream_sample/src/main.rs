
use mongodb::{Client, Collection, options::ClientOptions, bson::Document};

#[tokio::main]
async fn main() {
    let opt = ClientOptions::parse("mongodb://localhost").await.unwrap();
    let client = Client::with_options(opt).unwrap();

    let col: Collection<Document> = client.database("sample").collection("data");

    if let Ok(mut stream) = col.watch(None, None).await {
        while let Ok(r) = stream.next_if_any().await {
            if let Some(ev) = r {
                println!("{:?}", ev);
            }
        }
    } else {
        println!("failed");
    }
}
