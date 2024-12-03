use polars::prelude::*;
use polars_lazy::prelude::*;

use polars_lazy::frame::IntoLazy;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let path = "items.jsonl";

    let df = JsonLineReader::from_path(path)?.finish()?;

    println!("{:?}", df.schema());

    dbg!(&df);

    let df2 = df.lazy().filter(col("price").gt(lit(1500))).collect()?;

    dbg!(&df2);

    Ok(())
}
