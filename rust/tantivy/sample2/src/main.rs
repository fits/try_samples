
use std::env;
use std::fs::File;
use std::io::{BufRead, BufReader};

use tantivy::Index;
use tantivy::collector::TopDocs;
use tantivy::query::{QueryParser, RangeQuery};
use tantivy::schema::*;

fn main() -> tantivy::Result<()> {
    let file_lines = env::args()
        .skip(1)
        .next()
        .and_then(|f| File::open(f).ok())
        .map(|f| BufReader::new(f).lines());

    let mut schema_builder = Schema::builder();

    let _name = schema_builder.add_text_field("name", STRING | STORED);
    let color = schema_builder.add_text_field("color", STRING | STORED);
    let price = schema_builder.add_i64_field("price", INDEXED | STORED);
    let _date = schema_builder.add_date_field("date", INDEXED | STORED);

    let schema = schema_builder.build();
    let index = Index::create_in_ram(schema.clone());

    let mut index_writer = index.writer(3_000_000)?;

    for lines in file_lines {
        for line in lines {
            let doc = schema.parse_document(line?.as_str())?;
            index_writer.add_document(doc);
        }
    }

    index_writer.commit()?;

    let reader = index.reader()?;
    let searcher = reader.searcher();

    let query = QueryParser::for_index(&index, vec![color])
        .parse_query("white")?;

    let docs = searcher.search(&query, &TopDocs::with_limit(10))?;
    print_docs(&schema, &searcher, docs)?;

    println!("-----");

    let query = RangeQuery::new_i64(price, 300..2000);

    let docs = searcher.search(&query, &TopDocs::with_limit(5))?;
    print_docs(&schema, &searcher, docs)?;

    println!("-----");

    let query = QueryParser::for_index(&index, vec![])
        .parse_query("price:[300 TO 2000]")?;

    let docs = searcher.search(&query, &TopDocs::with_limit(10))?;
    print_docs(&schema, &searcher, docs)?;

    println!("-----");

    let query = QueryParser::for_index(&index, vec![])
        .parse_query("date:[2022-02-17T14:00:00+09:00 TO *] AND color:black")?;

    let docs = searcher.search(&query, &TopDocs::with_limit(10))?;
    print_docs(&schema, &searcher, docs)?;

    Ok(())
}

fn print_docs(schema: &Schema, searcher: &tantivy::Searcher, 
    docs: Vec<(f32, tantivy::DocAddress)>) -> tantivy::Result<()> {
    
    for (_score, address) in docs {
        let doc = searcher.doc(address)?;

        //println!("{:?}", doc);
        println!("{}", schema.to_json(&doc));
    }

    Ok(())
}