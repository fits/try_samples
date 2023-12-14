
#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = kube::Client::try_default().await?;

    let path = "/apis/metrics.k8s.io/v1beta1/pods";

    let req = http::Request::get(path).body(Default::default())?;

    let res = client.request::<serde_json::Value>(req).await?;

    println!("{}", res.to_string());

    Ok(())
}
