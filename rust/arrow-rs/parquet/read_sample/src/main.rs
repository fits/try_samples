use parquet::arrow::arrow_reader::ParquetRecordBatchReaderBuilder;

use std::{env, fs::File, path::Path};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let file_name = env::args().skip(1).next().unwrap();

    let path = Path::new(&file_name);
    let file = File::open(path)?;

    let reader = ParquetRecordBatchReaderBuilder::try_new(file)?
        .build()?;

    for r in reader {
        let b = r?;

        for s in b.schema().fields() {
            println!("schema field name={}", s.name());
        }

        println!("* RecordBatch cols={}, rows={}", b.num_columns(), b.num_rows());
        
        for c in b.columns() {
            println!("** {:?}", c);
        }
    }

    Ok(())
}
