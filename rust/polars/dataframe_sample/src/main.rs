
use polars::prelude::*;

fn main() -> Result<(), PolarsError> {
    let d = df!(
        "Name" => &["item1", "item2", "item3"],
        "Price" => &[500, 1000, 250],
    )?;

    println!("{}", d);

    let d2 = d.sort(["Price"], false)?;

    println!("{}", d2);

    let s = d2.get_columns()[1].clone();

    println!(
        "name={}, _dtype={}, max={:?}, mean={:?}", 
        s.name(), s._dtype(), s.max::<i32>(), s.mean()
    );

    Ok(())
}
