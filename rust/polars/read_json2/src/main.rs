use polars::frame::UniqueKeepStrategy;
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
        .clone()
        .lazy()
        .filter(
            col("attrs")
                .struct_()
                .field_by_name("category")
                .eq(lit("A1")),
        )
        .collect()?;

    dbg!(&df3);

    let df4 = df
        .clone()
        .lazy()
        .explode(["variants"])
        .unnest(["variants"])
        .collect()?;

    dbg!(&df4);

    let df4a = df.clone().lazy().explode(["variants"]).collect()?;

    dbg!(&df4a);

    let df5 = df
        .clone()
        .lazy()
        .explode(["variants"])
        .unnest(["variants"])
        .filter(col("color").eq(lit("white")))
        .select([col("id"), col("name")])
        .unique_stable(None, UniqueKeepStrategy::Any)
        .collect()?;

    dbg!(&df5);

    let df6 = df
        .clone()
        .lazy()
        .explode(["variants"])
        .unnest(["variants"])
        .filter(col("color").eq(lit("white")))
        .select([col("id"), col("name")])
        .unique_stable(None, UniqueKeepStrategy::None)
        .collect()?;

    dbg!(&df6);

    let df7 = df
        .clone()
        .lazy()
        .select([
            col("name"),
            col("variants")
                .list()
                .eval(
                    col("*").struct_().field_by_name("color").eq(lit("white")),
                    false,
                )
                .list()
                .contains(true),
        ])
        .collect()?;

    dbg!(&df7);

    let df8 = df
        .clone()
        .lazy()
        .filter(
            col("variants")
                .list()
                .eval(
                    col("").struct_().field_by_name("color").eq(lit("white")),
                    true,
                )
                .list()
                .contains(true),
        )
        .collect()?;

    dbg!(&df8);

    Ok(())
}
