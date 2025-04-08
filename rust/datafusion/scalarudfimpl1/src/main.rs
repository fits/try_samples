use datafusion::arrow::array::{AsArray, StringArray};
use datafusion::arrow::datatypes::DataType;
use datafusion::common::plan_err;
use datafusion::error::Result;
use datafusion::logical_expr::{
    ColumnarValue, ScalarFunctionArgs, ScalarUDF, ScalarUDFImpl, Signature, Volatility,
};
use datafusion::prelude::*;
use datafusion::scalar::ScalarValue;

use std::sync::Arc;

#[derive(Debug, Clone)]
struct Append {
    signature: Signature,
}

impl Append {
    fn new() -> Self {
        Self {
            signature: Signature::uniform(
                2,
                vec![DataType::Utf8, DataType::Utf8],
                Volatility::Immutable,
            ),
        }
    }
}

impl ScalarUDFImpl for Append {
    fn as_any(&self) -> &dyn std::any::Any {
        self
    }

    fn name(&self) -> &str {
        "append"
    }

    fn signature(&self) -> &Signature {
        &self.signature
    }

    fn return_type(&self, _arg_types: &[DataType]) -> Result<DataType> {
        Ok(DataType::Utf8)
    }

    fn invoke_with_args(&self, args: ScalarFunctionArgs) -> Result<ColumnarValue> {
        if args.args.len() == 2 {
            let a = args.args.first();
            let b = args.args.last();

            match (a, b) {
                (
                    Some(ColumnarValue::Scalar(ScalarValue::Utf8(a))),
                    Some(ColumnarValue::Scalar(ScalarValue::Utf8(b))),
                ) => {
                    let r = a.clone().and_then(|x| b.clone().map(|y| x + &y));
                    Ok(ColumnarValue::Scalar(ScalarValue::Utf8(r)))
                }
                (
                    Some(ColumnarValue::Array(a)),
                    Some(ColumnarValue::Scalar(ScalarValue::Utf8(b))),
                ) => {
                    let r = a
                        .as_string::<i32>()
                        .iter()
                        .map(|x| x.and_then(|x| b.clone().map(|y| x.to_string() + &y)))
                        .collect::<StringArray>();
                    Ok(ColumnarValue::Array(Arc::new(r)))
                }
                _ => plan_err!("unsupported arg types"),
            }
        } else {
            plan_err!("unsupported arg types")
        }
    }
}

#[tokio::main]
async fn main() -> Result<()> {
    let append_udf = ScalarUDF::from(Append::new());

    let ctx = SessionContext::new();
    ctx.register_udf(append_udf);

    run_query(&ctx, "SELECT append('ab', 'cdef')").await?;
    run_query(&ctx, "SELECT append(1234, 56)").await?;
    run_query(&ctx, "SELECT append(12, '-abc')").await?;
    run_query(&ctx, "SELECT append(12, ['a', 'b'])").await?;
    run_query(&ctx, "SELECT append([5, 6], ['a', 'b', 'c'])").await?;

    run_query(
        &ctx,
        "SELECT append(unnest(['a1', 'b22', 'c333']), '-sample')",
    )
    .await?;

    Ok(())
}

async fn run_query(ctx: &SessionContext, query: &str) -> Result<()> {
    let df = ctx.sql(query).await?;

    df.show().await?;

    Ok(())
}
