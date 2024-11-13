use std::env;
use tantivy::TantivyDocument;

mod core;
use core::Context;

fn main() -> tantivy::Result<()> {
    let path = env::args().nth(1).unwrap_or("./index".into());
    let data = env::args().nth(2).unwrap();

    let ctx = Context::new(path)?;

    let mut index_writer = ctx.index.writer(50_000_000)?;

    let doc = TantivyDocument::parse_json(&ctx.index.schema(), &data)?;

    let res = index_writer.add_document(doc)?;

    println!("result: {res}");

    index_writer.commit()?;

    Ok(())
}
