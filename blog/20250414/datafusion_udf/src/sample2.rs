use datafusion::arrow::array::{AsArray, StringArray};
use datafusion::arrow::datatypes::DataType;
use datafusion::common::plan_err;
use datafusion::error::Result;
use datafusion::logical_expr::{ColumnarValue, Volatility};
use datafusion::prelude::*;
use datafusion::scalar::ScalarValue;

use std::sync::Arc;

fn concat(args: &[ColumnarValue]) -> Result<ColumnarValue> {
    let (a, b) = (&args[0], &args[1]);

    match (a, b) {
        (
            ColumnarValue::Scalar(ScalarValue::Utf8(a)),
            ColumnarValue::Scalar(ScalarValue::Utf8(b)),
        ) => {
            let r = a.clone().and_then(|x| b.clone().map(|y| x + &y));
            Ok(ColumnarValue::Scalar(ScalarValue::Utf8(r)))
        }
        (ColumnarValue::Array(a), ColumnarValue::Scalar(ScalarValue::Utf8(b))) => {
            let r = a
                .as_string::<i32>()
                .iter()
                .map(|x| x.and_then(|x| b.clone().map(|y| x.to_string() + &y)))
                .collect::<StringArray>();
            Ok(ColumnarValue::Array(Arc::new(r)))
        }
        _ => plan_err!("unsupported arg types"),
    }
}

#[tokio::main]
async fn main() -> Result<()> {
    let udf = create_udf(
        "concat",
        vec![DataType::Utf8, DataType::Utf8],
        DataType::Utf8,
        Volatility::Immutable,
        Arc::new(concat),
    );

    let ctx = SessionContext::new();
    ctx.register_udf(udf);

    run_query(&ctx, "SELECT concat('ab', 'cdef')").await?;
    run_query(&ctx, "SELECT concat(1234, 56)").await?;
    run_query(&ctx, "SELECT concat([1, 2, 3], ['a', 'b'])").await?;

    run_query(
        &ctx,
        "SELECT concat(unnest(['a1', 'b22', 'c333']), '-sample')",
    )
    .await?;

    Ok(())
}

async fn run_query(ctx: &SessionContext, query: &str) -> Result<()> {
    let df = ctx.sql(query).await?;

    df.show().await?;

    Ok(())
}
