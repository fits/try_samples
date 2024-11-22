use std::env;
use std::time::Instant;

use datafusion::prelude::*;

#[tokio::main]
async fn main() -> datafusion::error::Result<()> {
    let path = env::args().nth(1).unwrap_or("./logs".into());
    let status_code = env::args().nth(2).unwrap_or("200".into());

    let t = Instant::now();

    let ctx = SessionContext::new();

    let opts = NdJsonReadOptions::default().file_extension(".jsonl");

    ctx.register_json("log", path, opts).await?;

    let t1 = t.elapsed();

    let query = format!(
        "SELECT time, remote_ip, request, response FROM log WHERE response = {status_code}"
    );

    let df = ctx.sql(&query).await?;

    let t2 = t.elapsed();

    df.show_limit(30).await?;

    let t3 = t.elapsed();

    println!("register_json: {}ms", t1.as_millis());
    println!("sql: {}ms", (t2 - t1).as_millis());
    println!("show: {}ms", (t3 - t2).as_millis());

    Ok(())
}
