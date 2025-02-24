use datafusion::arrow::array::{new_empty_array, AsArray};
use datafusion::arrow::datatypes::{DataType, Field};
use datafusion::common::{plan_err, utils::arrays_into_list_array};
use datafusion::logical_expr::{
    ColumnarValue, ScalarFunctionArgs, ScalarUDF, ScalarUDFImpl, Signature, Volatility,
};
use datafusion::prelude::*;

use std::env;
use std::sync::Arc;

#[derive(Debug, Clone)]
struct ExtractColumn {
    name: String,
    signature: Signature,
    column: Field,
}

impl ExtractColumn {
    fn try_new(list_column: &Field, column_name: &str) -> datafusion::error::Result<Self> {
        match list_column.data_type() {
            DataType::List(s) => match s.data_type() {
                DataType::Struct(ff) => {
                    match ff.filter_leaves(|_, x| x.name() == column_name).first() {
                        Some(f) => {
                            let signature = Signature::exact(
                                vec![list_column.data_type().clone()],
                                Volatility::Immutable,
                            );

                            Ok(Self {
                                name: format!("extract_{column_name}"),
                                signature,
                                column: f.as_ref().clone(),
                            })
                        }
                        None => plan_err!("not found column"),
                    }
                }
                _ => plan_err!("not supported datatype"),
            },
            _ => plan_err!("not supported datatype"),
        }
    }
}

impl ScalarUDFImpl for ExtractColumn {
    fn as_any(&self) -> &dyn std::any::Any {
        self
    }

    fn name(&self) -> &str {
        &self.name
    }

    fn signature(&self) -> &datafusion::logical_expr::Signature {
        &self.signature
    }

    fn return_type(&self, _arg_types: &[DataType]) -> datafusion::error::Result<DataType> {
        Ok(DataType::List(Arc::new(
            self.column.clone().with_name("item"),
        )))
    }

    fn invoke_with_args(
        &self,
        args: ScalarFunctionArgs,
    ) -> datafusion::error::Result<ColumnarValue> {
        if let Some(v) = args.args.first() {
            match v {
                ColumnarValue::Array(a) => {
                    let r = a.as_list::<i32>().iter().map(|x| {
                        x.and_then(|y| y.as_struct().column_by_name(&self.column.name()).cloned())
                            .unwrap_or(new_empty_array(self.column.data_type()))
                    });

                    Ok(ColumnarValue::Array(Arc::new(arrays_into_list_array(r)?)))
                }
                _ => {
                    plan_err!("invalid arguments")
                }
            }
        } else {
            plan_err!("empty args")
        }
    }
}

#[tokio::main]
async fn main() -> datafusion::error::Result<()> {
    let path = env::args().skip(1).next().unwrap_or("./data".into());

    let ctx = SessionContext::new();

    let opts = NdJsonReadOptions::default().file_extension(".jsonl");

    ctx.register_json("items", path, opts).await?;

    let t = ctx.table("items").await?;
    let variants = t.schema().field_with_name(None, "variants")?;

    ctx.register_udf(ScalarUDF::from(ExtractColumn::try_new(variants, "color")?));

    run_query(&ctx, "SELECT extract_color(variants) FROM items").await?;

    run_query(
        &ctx,
        "SELECT extract_color(variants), array_has(extract_color(variants), 'white') FROM items",
    )
    .await?;

    run_query(
        &ctx,
        "SELECT id, name FROM items WHERE array_has(extract_color(variants), 'white')",
    )
    .await?;

    Ok(())
}

async fn run_query(ctx: &SessionContext, query: &str) -> datafusion::error::Result<()> {
    let df = ctx.sql(query).await?;

    df.show().await?;

    Ok(())
}
