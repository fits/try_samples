use std::env;

use datafusion::prelude::*;

#[tokio::main]
async fn main() -> datafusion::error::Result<()> {
    let path = env::args().skip(1).next().unwrap_or("./data".into());

    let ctx = SessionContext::new();

    let opts = NdJsonReadOptions::default().file_extension(".jsonl");

    ctx.register_json("data", path, opts).await?;

    run_query(&ctx, "SELECT name, value FROM data WHERE value > 50").await?;

    run_query(&ctx, "SELECT name FROM data WHERE type = 'test1'").await?;

    run_query(
        &ctx,
        "SELECT SUM(value) AS total FROM data WHERE name LIKE 'data%' AND type IS NOT NULL",
    )
    .await?;

    Ok(())
}

async fn run_query(ctx: &SessionContext, query: &str) -> datafusion::error::Result<()> {
    let df = ctx.sql(query).await?;

    df.show().await?;

    Ok(())
}
