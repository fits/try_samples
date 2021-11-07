
use tantivy::{doc, Index};
use tantivy::collector::TopDocs;
use tantivy::query::QueryParser;
use tantivy::schema::*;

fn main() -> tantivy::Result<()> {
    let mut schema_builder = Schema::builder();

    let name = schema_builder.add_text_field("name", TEXT | STORED);
    let category = schema_builder.add_text_field("category", TEXT);
    let comment = schema_builder.add_text_field("comment", STORED);

    let schema = schema_builder.build();
    let index = Index::create_in_ram(schema.clone());

    let mut index_writer = index.writer(3_000_000)?;

    index_writer.add_document(doc!(
        name => "sample-1", category => "a_test", comment => "comment1"
    ));
    index_writer.add_document(doc!(
        name => "sample12345", category => "b_test", comment => "comment2"
    ));
    index_writer.add_document(doc!(
        name => "sample-2", category => "c_test", comment => "comment3"
    ));
    index_writer.add_document(doc!(
        name => "item-123", category => "d_sample", comment => "comment4"
    ));

    index_writer.commit()?;

    let reader = index.reader()?;
    let searcher = reader.searcher();

    let query_parser = QueryParser::for_index(&index, vec![name, category]);

    let query = query_parser.parse_query("sample")?;

    let docs = searcher.search(&query, &TopDocs::with_limit(10))?;

    for (_score, address) in docs {
        let doc = searcher.doc(address)?;

        println!("{:?}", doc);
        println!("{}", schema.to_json(&doc));
    }

    Ok(())
}
