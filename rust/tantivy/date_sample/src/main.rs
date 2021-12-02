
use tantivy::doc;
use tantivy::schema::*;
use chrono::prelude::*;

fn main() {
    let mut schema_builder = Schema::builder();

    let name = schema_builder.add_text_field("name", STRING);
    let date = schema_builder.add_date_field("date", INDEXED);

    let schema = schema_builder.build();

    let doc = doc!(
        name => "item1", date => Utc::now()
    );

    let json = schema.to_json(&doc);

    println!("{}", json);
}
