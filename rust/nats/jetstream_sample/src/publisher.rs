
use async_nats::jetstream;
use std::env;

#[tokio::main]
async fn main() -> Result<(), async_nats::Error> {
    let nats_url = env::var("NATS_URL").unwrap_or("nats://127.0.0.1".to_string());

    let client = async_nats::connect(nats_url).await.unwrap();
    let context = async_nats::jetstream::new(client);

    let s_cfg = jetstream::stream::Config {
        name: "js-sample".to_string(),
        subjects: vec!["js-sample.*".to_string()],
        ..Default::default()
    };

    context.create_stream(s_cfg).await?;

    let subject = "js-sample.step1".to_string();
    let payload = env::args().skip(1).next().unwrap();

    let ack = context.publish(subject, payload.into()).await?;

    let r = ack.await?;
    println!("published: {:?}", r);

    Ok(())
}
