use std::fs::File;
use std::sync::Arc;
use std::{env, io::BufReader};

use arrow_json::ReaderBuilder;
use arrow_schema::{DataType, Field, Schema};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let file_name = env::args().skip(1).next().ok_or("input jsonl file")?;

    let schema = Arc::new(Schema::new(vec![
        Field::new("name", DataType::Utf8, false),
        Field::new("value", DataType::Int32, false),
    ]));

    let file = File::open(file_name)?;

    let reader = BufReader::new(file);
    let mut json = ReaderBuilder::new(schema).build(reader)?;

    let r = json.next().ok_or("no json")??;

    for c in r.columns() {
        println!("column: {c:?}");
    }

    Ok(())
}
