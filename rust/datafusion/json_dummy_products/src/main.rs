use std::env;

use datafusion::prelude::*;

#[tokio::main]
async fn main() -> datafusion::error::Result<()> {
    let path = env::args().skip(1).next().unwrap_or("./data".into());

    let ctx = SessionContext::new();

    let opts = NdJsonReadOptions::default().file_extension(".jsonl");

    let df = ctx.read_json(path, opts).await?;

    for f in df.schema().fields() {
        println!("{f}");
    }

    println!("-----");

    df.clone()
        .filter(col("id").eq(lit(20)))?
        .select(vec![col("id"), col("title")])?
        .show()
        .await?;

    df.clone()
        .filter(ident("id").eq(lit(20)))?
        .select(vec![col("id"), col("title")])?
        .show()
        .await?;

    /* FieldNotFound Error (datafusion 43.0.0) */
    // df.clone().unnest_columns(&["meta"])?.filter(col("meta.barcode").eq(lit("8400326844874")))?.show().await?;
    // df.clone().unnest_columns(&["meta"])?.filter(ident("meta.barcode").eq(lit("8400326844874")))?.show().await?;

    let df2 = df
        .clone()
        .unnest_columns(&["meta"])?
        .select_columns(&["meta.barcode"])?;

    println!(
        "* meta.barcode field = {:?}",
        df2.schema().field_with_name(None, "meta.barcode")
    ); // Ok

    /* FieldNotFound Error (datafusion 43.0.0) */
    df2.filter(ident("meta.barcode").eq(lit("8400326844874")))?
        .show()
        .await?;

    Ok(())
}
