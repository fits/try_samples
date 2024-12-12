use datafusion::arrow::array::{AsArray, StringArray};
use datafusion::arrow::datatypes::DataType;
use datafusion::common::{internal_err, utils::arrays_into_list_array};
use datafusion::logical_expr::{ColumnarValue, Volatility};
use datafusion::prelude::*;

use std::env;
use std::sync::Arc;

#[tokio::main]
async fn main() -> datafusion::error::Result<()> {
    let path = env::args().skip(1).next().unwrap_or("./data".into());

    let ctx = SessionContext::new();

    let opts = NdJsonReadOptions::default().file_extension(".jsonl");

    ctx.register_json("items", path, opts).await?;

    let extract_color = Arc::new(|args: &[ColumnarValue]| match &args[0] {
        ColumnarValue::Array(a) => {
            let r = a.as_list::<i32>().iter().map(|x| {
                x.and_then(|y| y.as_struct().column_by_name("color").cloned())
                    .unwrap_or(Arc::new(StringArray::new_null(0)))
            });

            Ok(ColumnarValue::Array(Arc::new(arrays_into_list_array(r)?)))
        }
        _ => {
            internal_err!("invalid argument types")
        }
    });

    let t = ctx.table("items").await?;
    let variants_f = t.schema().field_with_name(None, "variants")?;

    if let DataType::List(s) = variants_f.data_type() {
        if let DataType::Struct(f) = s.data_type() {
            if let Some(color_f) = f.filter_leaves(|_, x| x.name() == "color").first() {
                let udf = create_udf(
                    "extract_color",
                    vec![variants_f.data_type().clone()],
                    DataType::List(Arc::new(color_f.as_ref().clone().with_name("item"))),
                    Volatility::Immutable,
                    extract_color,
                );

                ctx.register_udf(udf);

                run_query(&ctx, "SELECT extract_color(variants) FROM items").await?;

                run_query(&ctx, "SELECT extract_color(variants), array_has(extract_color(variants), 'white') FROM items").await?;

                run_query(
                    &ctx,
                    "SELECT id, name FROM items WHERE array_has(extract_color(variants), 'white')",
                )
                .await?;
            }
        }
    }

    Ok(())
}

async fn run_query(ctx: &SessionContext, query: &str) -> datafusion::error::Result<()> {
    let df = ctx.sql(query).await?;

    df.show().await?;

    Ok(())
}
