
use surrealdb_types::SurrealValue;
use surrealism::surrealism;

#[derive(Debug, SurrealValue)]
struct Data {
    name: String,
    value: isize,
}

#[surrealism(writeable)]
fn create_data(d: Data) -> Result<String, String> {
    if d.value >= 1 {
        Ok(format!("created data: {d:?}").into())
    } else {
        Err(format!("invalid value {} < 1", d.value).into())
    }
}

#[surrealism]
fn check_value(v: isize) -> bool {
    v >= 1
}