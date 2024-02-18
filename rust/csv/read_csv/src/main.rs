use csv::Reader;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let file = std::env::args().skip(1).next().ok_or("csv file")?;

    let mut reader = Reader::from_path(file)?;

    if reader.has_headers() {
        println!("headers = {:?}", reader.headers()?);
    }

    for r in reader.records() {
        let rec = r?;
        println!("{:?}", rec);
    }

    Ok(())
}
