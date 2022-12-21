
use mongodb::{
    Client, Collection,  
    bson::Document, 
    options::{ClientOptions, ChangeStreamOptions},
    change_stream::event::{ChangeStreamEvent, ResumeToken}
};

use std::{
    env,
    fs::File, 
    io::{BufReader, BufWriter}
};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error + Send + Sync>>;

#[tokio::main]
async fn main() -> Result<()> {
    let token_file = env::var("TOKEN_FILE").unwrap_or("resume_token.json".to_string());

    let nats_endpoint = env::var("NATS_ENDPOINT").unwrap_or("nats://127.0.0.1".to_string());
    let mongo_endpoint = env::var("MONGO_ENDPOINT").unwrap_or("mongodb://127.0.0.1".to_string());

    let db_name = env::var("DB_NAME").unwrap_or("delivery".to_string());
    let col_name = env::var("COLLECTION_NAME").unwrap_or("data".to_string());

    let nats_client = async_nats::connect(nats_endpoint).await?;
    let jetstream = async_nats::jetstream::new(nats_client);

    let cfg = async_nats::jetstream::stream::Config {
        name: "delivery-event".to_string(),
        subjects: vec!["delivery-event.*".to_string()],
        ..Default::default()
    };

    jetstream.create_stream(cfg).await?;

    let opt = ClientOptions::parse(mongo_endpoint).await?;
    let mongo_client = Client::with_options(opt)?;

    let col: Collection<Document> = mongo_client.database(&db_name).collection(&col_name);

    let token = read_token(&token_file).ok();

    if let Some(_) = token {
        println!("resume after");
    }

    let opts = ChangeStreamOptions::builder()
        .resume_after(token)
        .build();
    
    let mut stream = col.watch(None, opts).await?;

    while let Ok(r) = stream.next_if_any().await {
        if let Some(ev) = r {
            handle_stream_event(&ev, &jetstream).await?;

            save_token(&token_file, &ev.id)?;
        }
    }

    Ok(())
}

async fn handle_stream_event(event: &ChangeStreamEvent<Document>, jetstream: &async_nats::jetstream::Context) -> Result<()> {
    let event = find_event(event);

    if let Some(d) = event {
        let subject = d.keys()
            .next()
            .map(|n| format!("delivery-event.{}", n.to_lowercase()))
            .unwrap_or("delivery-event.unknown".to_string());

        let payload = serde_json::to_string(d)?;

        println!("publish: subject={}, payload={}", subject, payload);

        let ack_f = jetstream.publish(subject, payload.into()).await?;
        let ack = ack_f.await?;

        println!("published: ack={:?}", ack);
    }

    Ok(())
}

fn find_event(event: &ChangeStreamEvent<Document>) -> Option<&Document> {
    if let Some(d) = &event.full_document {
        d.get_array("events")
            .ok()
            .and_then(|v| v.last())
            .and_then(|b| b.as_document())
            .and_then(|d| d.get_document("event").ok())
    } else if let Some(u) = &event.update_description {
        u.updated_fields
            .keys()
            .find(|&k| k.starts_with("events."))
            .and_then(|k| u.updated_fields.get_document(k).ok())
            .and_then(|d| d.get_document("event").ok())
    } else {
        None
    }
}

fn read_token(file: &str) -> Result<ResumeToken> {
    let f = File::open(file)?;
    let rdr = BufReader::new(f);

    let r = serde_json::from_reader(rdr)?;

    Ok(r)
}

fn save_token(file: &str, token: &ResumeToken) -> Result<()> {
    let f = File::create(file)?;
    let wrt = BufWriter::new(f);

    serde_json::to_writer(wrt, token)?;

    Ok(())
}
