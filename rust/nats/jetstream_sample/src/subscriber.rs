
use async_nats::jetstream;
use tokio_stream::StreamExt;

use std::{env, str::from_utf8};

#[tokio::main]
async fn main() -> Result<(), async_nats::Error> {
    let nats_url = env::var("NATS_URL").unwrap_or("nats://127.0.0.1".to_string());
    let durable_name = env::var("DURABLE_NAME").unwrap_or("consumer1".to_string());

    let client = async_nats::connect(nats_url).await.unwrap();
    let context = jetstream::new(client);

    let s_cfg = jetstream::stream::Config {
        name: "js-sample".to_string(),
        subjects: vec!["js-sample.*".to_string()],
        ..Default::default()
    };

    let stream = context.create_stream(s_cfg).await?;

    let c_cfg = jetstream::consumer::pull::Config {
        durable_name: Some(durable_name),
        ..Default::default()
    };

    let consumer = stream.create_consumer(c_cfg).await?;

    let mut messages = consumer.messages().await?;

    while let Some(r) = messages.next().await {
        if let Ok(m) = r {
            let payload = from_utf8(&m.payload)?;
            println!("subject={}, payload={}", m.subject, payload);

            m.ack().await?;
        }
    }

    Ok(())
}
