use datafusion::prelude::*;
use std::env;

#[tokio::main]
async fn main() -> datafusion::error::Result<()> {
    let path = env::args().skip(1).next().unwrap_or("./data".into());

    let ctx = SessionContext::new();

    let opts = NdJsonReadOptions::default().file_extension(".jsonl");

    ctx.register_json("items", path, opts).await?;

    run_query(
        &ctx,
        "SELECT id, attrs.code, attrs, variants FROM items WHERE id < 4",
    )
    .await?;

    run_query(
        &ctx,
        "SELECT id, name, attrs.category FROM items WHERE attrs.category = 'A1'",
    )
    .await?;

    run_query(
        &ctx,
        "
        SELECT DISTINCT id, name
        FROM
            (SELECT id, name, unnest(variants) AS v FROM items)
        WHERE
            v.color = 'white'
        ",
    )
    .await?;

    run_query(
        &ctx,
        "
        WITH x AS (SELECT id, name, unnest(variants) AS v FROM items)
        SELECT DISTINCT id, name FROM x WHERE v.color = 'white' 
        ",
    )
    .await?;

    run_query(
        &ctx,
        "
        SELECT id, name, unnest(variants)['color'] AS color FROM items
        ",
    )
    .await?;

    run_query(
        &ctx,
        "
        WITH x AS (SELECT id, name, unnest(variants)['color'] AS color FROM items)
        SELECT DISTINCT id, name FROM x WHERE color = 'white'
        ",
    )
    .await?;

    Ok(())
}

async fn run_query(ctx: &SessionContext, query: &str) -> datafusion::error::Result<()> {
    let df = ctx.sql(query).await?;

    df.show().await?;

    Ok(())
}