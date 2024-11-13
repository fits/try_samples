use std::env;
use tantivy::{collector::TopDocs, query::QueryParser, Document, TantivyDocument};

mod core;
use core::Context;

fn main() -> tantivy::Result<()> {
    let n = 10;
    let path = env::args().nth(1).unwrap_or("./index".into());
    let query = env::args().nth(2).unwrap();

    let ctx = Context::new(path)?;

    let reader = ctx.index.reader()?;
    let searcher = reader.searcher();

    let schema = ctx.index.schema();

    if let Some((field, _entry)) = schema.fields().next() {
        let query_parser = QueryParser::for_index(&ctx.index, vec![field]);
        {
            let q = query_parser.parse_query(&query)?;
            let docs = searcher.search(&q, &TopDocs::with_limit(n))?;

            for (score, address) in docs {
                let doc: TantivyDocument = searcher.doc(address)?;
                let doc_json = doc.to_json(&schema);

                println!("score={score}, doc={doc_json}");
            }
        }
    }

    Ok(())
}
