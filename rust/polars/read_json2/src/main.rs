use polars_lazy::prelude::*;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let path = "items.jsonl";

    let df = LazyJsonLineReader::new(path).finish()?.collect()?;

    println!("{:?}", df.schema());
    dbg!(&df);

    let df2 = df
        .clone()
        .lazy()
        .filter(col("price").gt(lit(1500)))
        .collect()?;

    dbg!(&df2);

    let df3 = df
        .lazy()
        .filter(
            col("attrs")
                .struct_()
                .field_by_name("category")
                .eq(lit("A1")),
        )
        .collect()?;

    dbg!(&df3);

    Ok(())
}
