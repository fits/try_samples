use std::env;
use std::fs::File;
use std::io::{BufRead, BufReader};
use std::time::Instant;

fn main() -> Result<(), std::io::Error> {
    let args = env::args().collect::<Vec<_>>();
    let file = &args[1];
    let term = &args[2];

    let t1 = Instant::now();

    let ds = load_lines(file)?;

    let t2 = Instant::now();

    let rs = filter(&ds, term);

    let t3 = Instant::now();

    println!("total size = {}, filtered size = {}", ds.len(), rs.len());
    println!("read time = {:?}", t2 - t1);
    println!("search time = {:?}", t3 - t2);

    Ok(())
}

fn load_lines(file: &str) -> Result<Vec<String>, std::io::Error> {
    let mut res = vec![];

    for line in BufReader::new(File::open(file)?).lines() {
        res.push(line?);
    } 

    Ok(res)
}

fn filter<'a>(ds: &'a Vec<String>, term: &'a str) -> Vec<&'a String> {
    ds
        .iter()
        .filter(|d| d.contains(term))
        .collect()
}
