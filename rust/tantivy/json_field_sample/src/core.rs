use tantivy::schema::{Schema, STORED, TEXT};
use tantivy::Index;

pub struct Context {
    pub index: Index,
}

impl Context {
    pub fn new(path: String) -> tantivy::Result<Self> {
        let index = match Index::open_in_dir(&path) {
            Ok(index) => index,
            Err(_e) => {
                let mut schema_builder = Schema::builder();

                schema_builder.add_json_field("data", TEXT | STORED);

                let schema = schema_builder.build();

                Index::create_in_dir(&path, schema)?
            }
        };

        Ok(Self { index })
    }
}
