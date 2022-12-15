
use mongodb::{
    Client, Collection, options::{ClientOptions, ChangeStreamOptions}, 
    bson::Document, change_stream::event::ResumeToken
};
use std::env;

#[tokio::main]
async fn main() {
    let mongo_endpoint = env::var("MONGO_ENDPOINT").unwrap_or("mongodb://127.0.0.1".to_string());

    let db_name = env::var("DB_NAME").unwrap_or("sample".to_string());
    let col_name = env::var("COLLECTION_NAME").unwrap_or("data".to_string());

    let opt = ClientOptions::parse(mongo_endpoint).await.unwrap();
    let client = Client::with_options(opt).unwrap();

    let col: Collection<Document> = client.database(&db_name).collection(&col_name);

    let token: Option<ResumeToken> = env::args()
        .skip(1)
        .next()
        .and_then(|t| serde_json::from_str(&t).ok());

    println!("resume token: {:?}", token);

    let opts = ChangeStreamOptions::builder()
        .resume_after(token)
        .build();

    if let Ok(mut stream) = col.watch(None, opts).await {
        while let Ok(r) = stream.next_if_any().await {
            if let Some(ev) = r {
                println!("-----");
                println!("*** token = {}", serde_json::to_string(&ev.id).unwrap_or("".to_string()));
                println!("*** update_description = {:?}", ev.update_description);
                println!("*** full_document = {:?}", ev.full_document);
            }
        }
    } else {
        println!("failed");
    }
}
