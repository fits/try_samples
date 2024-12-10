use datafusion::arrow::array::{ArrayRef, AsArray, RecordBatch, StringArray};
use datafusion::arrow::datatypes::DataType;
use datafusion::common::internal_err;
use datafusion::logical_expr::{ColumnarValue, Volatility};
use datafusion::prelude::*;
use datafusion::scalar::ScalarValue;
use std::sync::Arc;

fn append(args: &[ColumnarValue]) -> datafusion::error::Result<ColumnarValue> {
    let (a, b) = (&args[0], &args[1]);

    println!("{a:?}, {b:?}");

    match (a, b) {
        (
            ColumnarValue::Scalar(ScalarValue::Utf8(a)),
            ColumnarValue::Scalar(ScalarValue::Utf8(b)),
        ) => {
            let r = a.clone().unwrap_or_default() + &b.clone().unwrap_or_default();
            Ok(ColumnarValue::Scalar(ScalarValue::from(r)))
        }
        (ColumnarValue::Array(a_array), ColumnarValue::Scalar(ScalarValue::Utf8(b))) => {
            let b = b.clone().unwrap_or_default();

            let r = a_array
                .as_string::<i32>()
                .iter()
                .map(|x| x.map(|y| y.to_string() + &b))
                .collect::<StringArray>();

            Ok(ColumnarValue::Array(Arc::new(r)))
        }
        _ => {
            internal_err!("invalid argument types")
        }
    }
}

#[tokio::main]
async fn main() -> datafusion::error::Result<()> {
    let ctx = SessionContext::new();

    let udf = create_udf(
        "append",
        vec![DataType::Utf8, DataType::Utf8],
        DataType::Utf8,
        Volatility::Immutable,
        Arc::new(append),
    );

    ctx.register_udf(udf);

    run_query(&ctx, "SELECT append('ab', 'cde')").await?;
    run_query(&ctx, "SELECT append('ab', 123)").await?;
    run_query(&ctx, "SELECT append(120, 345)").await?;
    run_query(&ctx, "SELECT append('test-', [1, 2, 3])").await?;

    let name: ArrayRef = Arc::new(StringArray::from(vec![Some("a1"), Some("b2"), Some("c3"), None, Some("d4")]));
    let batch = RecordBatch::try_from_iter(vec![("name", name)])?;

    ctx.register_batch("data", batch)?;

    run_query(&ctx, "SELECT append(name, '-suffix') FROM data").await?;

    Ok(())
}

async fn run_query(ctx: &SessionContext, query: &str) -> datafusion::error::Result<()> {
    let df = ctx.sql(query).await?;

    df.show().await?;

    Ok(())
}
