
use std::env;
use std::fs::File;
use std::io::{BufRead, BufReader};
use std::time::Instant;

use serde_json::{Map as JsonObject, Value as JsonValue};

type TResult<T> = Result<T, Box<dyn std::error::Error>>;
type Document = JsonObject<String, JsonValue>;

#[derive(Clone, Debug)]
struct Filter {
    field: String,
    query: String,
}

fn main() -> TResult<()> {
    let args = env::args().collect::<Vec<_>>();

    let fs = parse_query(&args[2]);

    let t1 = Instant::now();

    let docs = load_logs(&args[1])?;

    let t2 = Instant::now();

    let rs = search(&docs, &fs);

    let t3 = Instant::now();

    println!("logs size = {}", docs.len());
    println!("load time = {:?}", t2 - t1);

    println!("search logs size = {}", rs.len());
    println!("search time = {:?}", t3 - t2);

    Ok(())
}

fn parse_query(query: &str) -> Vec<Filter> {
    query
        .split(",")
        .map(|q| {
            let s = q.split(":").collect::<Vec<_>>();
            
            Filter{
                field: s[0].trim().to_string(), 
                query: s[1].trim().to_lowercase(),
            }
        })
        .collect()
}

fn load_logs(file: &str) -> TResult<Vec<Document>> {
    let file_lines = BufReader::new(File::open(file)?).lines();

    let mut rs = vec![];

    for line in file_lines {
        let v = serde_json::from_str(&line?)?;
        rs.push(v);
    }

    Ok(rs)
}

fn search(docs: &Vec<Document>, fs: &Vec<Filter>) -> Vec<Document> {
    docs
        .iter()
        .flat_map(|doc| {
            fs
                .iter()
                .all(|Filter{field, query}| 
                    doc
                        .get(field)
                        .map(|v| match v {
                            JsonValue::String(s) => s.to_lowercase(),
                            JsonValue::Number(n) => n.to_string(),
                            _ => format!("{}", v).to_lowercase(),
                        })
                        .map(|s| s.contains(query))
                        .unwrap_or(false)
                )
                .then(|| vec![doc.clone()])
                .unwrap_or(vec![])
        })
        .collect()
}
