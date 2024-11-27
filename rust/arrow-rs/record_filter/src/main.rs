use arrow_array::{record_batch, BooleanArray, RecordBatch, UInt32Array};
use arrow_json::ArrayWriter;
use arrow_select::filter::filter_record_batch;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let batch = record_batch!(
        ("id", Int32, [1, 2, 3, 4, 5]),
        ("name", Utf8, ["a1", "b2", "c3", "D4", "e5"]),
        ("value", UInt32, [1, 22, 333, 4, 55])
    )?;

    let vs = batch
        .column_by_name("value")
        .and_then(|v| v.as_any().downcast_ref::<UInt32Array>())
        .ok_or("failed get column")?;

    let pred = BooleanArray::from_iter(vs.iter().map(|x| x.map(|y| y > 20)));

    let batch2 = filter_record_batch(&batch, &pred)?;

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
