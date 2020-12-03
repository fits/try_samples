
use cloudevents::{EventBuilder, EventBuilderV10};
use cloudevents_sdk_reqwest::RequestBuilderExt;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let url = "http://localhost:3000";

    let data = r#"
        {
            "value": "r1"
        }
    "#;

    let event = EventBuilderV10::default()
        .source("example:sender-rust")
        .ty("sample.type")
        .data("application/json", data.to_string())
        .build()?;

    println!("event: {:?}", event);

    let r = reqwest::Client::new()
        .post(url)
        .event(event)?
        .send()
        .await?;

    println!("result: {:?}", r);

    Ok(())
}