
use std::sync::Arc;

use tide::{Body, Request, Response, StatusCode};
use tantivy::{Index, ReloadPolicy};
use tantivy::collector::TopDocs;
use tantivy::query::QueryParser;
use tantivy::schema::*;

#[derive(Clone)]
struct Context {
    index: Arc<Index>,
}

#[async_std::main]
async fn main() -> tide::Result<()> {
    let mut schema_builder = Schema::builder();

    schema_builder.add_text_field("name", TEXT | STORED);
    schema_builder.add_text_field("category", TEXT | STORED);
    schema_builder.add_text_field("comment", STORED);

    let schema = schema_builder.build();
    let index = Index::create_in_ram(schema.clone());

    let ctx = Context { index: Arc::new(index) };

    let mut app = tide::with_state(ctx);

    app.at("/update").post(update);
    app.at("/search/:q").get(search);

    app.listen("127.0.0.1:8080").await?;

    Ok(())
}

async fn update(mut req: Request<Context>) -> tide::Result {
    let ctx: &Context = req.state();

    let index = ctx.index.clone();
    let schema = index.schema();

    let mut index_writer = index.writer(3_000_000)?;

    let body: String = req.body_string().await?;
    println!("request body = {}", body);

    let doc = schema.parse_document(body.as_str())?;

    index_writer.add_document(doc);
    index_writer.commit()?;

    Ok(Response::from(StatusCode::Ok))
}

async fn search(req: Request<Context>) -> tide::Result {
    let q = req.param("q").unwrap_or("");

    println!("query = {}", q);

    let ctx: &Context = req.state();

    let index = ctx.index.clone();

    let schema = index.schema();

    let reader = index
        .reader_builder()
        .reload_policy(ReloadPolicy::OnCommit)
        .try_into()?;

    let searcher = reader.searcher();

    let name = schema.get_field("name").unwrap();
    let category = schema.get_field("category").unwrap();

    let query_parser = QueryParser::for_index(&index, vec![name, category]);

    let query = query_parser.parse_query(q)?;

    let addrs = searcher.search(&query, &TopDocs::with_limit(10))?;

    let docs = addrs
        .iter()
        .map(|(_, addr)|{
            let doc = searcher.doc(addr.clone()).unwrap();
            schema.to_named_doc(&doc)
        })
        .collect::<Vec<_>>();

    Body::from_json(&docs)
        .map(Response::from)
}
