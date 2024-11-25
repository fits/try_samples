use std::env;

use datafusion::prelude::*;

#[tokio::main]
async fn main() -> datafusion::error::Result<()> {
    let path = env::args().nth(1).unwrap_or("./data".into());

    let ctx = SessionContext::new();

    let opts = NdJsonReadOptions::default().file_extension(".jsonl");

    ctx.register_json("product", path, opts).await?;

    run_query(
        &ctx,
        "SELECT id, title, meta.barcode as barcode FROM product WHERE meta.barcode='0726316339746'",
    )
    .await?;

    run_query(
        &ctx,
        "
        SELECT
            id, title 
        FROM
            (SELECT id, title, unnest(reviews) as reviews FROM product)
        WHERE
            reviews.rating = 1
        ",
    )
    .await?;

    run_query(
        &ctx,
        "
        SELECT DISTINCT
            id, title 
        FROM
            (SELECT id, title, unnest(reviews) as reviews FROM product)
        WHERE
            reviews.rating = 1
        ",
    )
    .await?;

    run_query(
        &ctx,
        "
        WITH x AS (SELECT id, title, unnest(reviews) as reviews FROM product)
        SELECT DISTINCT id, title FROM x WHERE reviews.rating = 1
        ",
    )
    .await?;

    run_query(
        &ctx,
        "
        WITH x AS (SELECT id, title, unnest(reviews) as reviews FROM product)
        SELECT DISTINCT id, title FROM x WHERE reviews.rating = 1 ORDER BY id
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
