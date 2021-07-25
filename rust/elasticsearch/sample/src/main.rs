
use elasticsearch::{
    Elasticsearch,
    IndexParts,
    SearchParts,
    params::Refresh,
};
use serde_json::{json, Value};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Elasticsearch::default();

    let res = client
        .index(IndexParts::Index("sample"))
        .body(json!({
            "name": "test1",
            "value": 123
        }))
        .refresh(Refresh::True)
        .send()
        .await?;

    println!("{:?}", res);

    let s_res = client
        .search(SearchParts::Index(&["sample"]))
        .body(json!({
            "query": {
                "match_all": {}
            }
        }))
        .send()
        .await?;

    let body = s_res.json::<Value>().await?;

    println!("{:?}", body);

    Ok(())
}
