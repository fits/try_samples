use arrow_array::{record_batch, RecordBatch};
use arrow_json::ArrayWriter;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let batch = record_batch!(
        ("id", Int32, [1, 2, 3]),
        ("name", Utf8, ["a1", "b2", "c3"]),
        ("value", UInt32, [1, 22, 333])
    )?;

    println!("{batch:?}");
    println!("{}", to_json(&batch)?);

    println!("-----");

    let batch2 = batch.slice(1, 1);

    println!("{batch2:?}");
    println!("{}", to_json(&batch2)?);

    Ok(())
}

fn to_json(batch: &RecordBatch) -> Result<String> {
    let mut writer = ArrayWriter::new(vec![]);

    writer.write_batches(&vec![batch])?;

    writer.finish()?;

    let res = String::from_utf8(writer.into_inner())?;

    Ok(res)
}
