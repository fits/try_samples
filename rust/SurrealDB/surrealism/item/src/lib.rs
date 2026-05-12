use anyhow::{Result, anyhow};
use surrealdb_types::{RecordId, SurrealValue, Value};
use surrealism::surrealism;

#[derive(Debug, SurrealValue)]
struct ItemInput {
    name: String,
    qty: i32,
}

#[derive(Debug, Clone, SurrealValue)]
struct Item {
    id: RecordId,
    name: String,
    qty: i32,
}

#[surrealism(writeable)]
fn add_item(input: ItemInput) -> Result<RecordId> {
    if check_qty(input.qty) {
        let params = vec![
            ("name".into(), Value::String(input.name)),
            ("qty".into(), Value::Number(input.qty.into())),
        ];

        surrealism::sql_with_vars(
            "CREATE items SET name=$name, qty=$qty RETURN VALUE id",
            params,
        )
    } else {
        Err(anyhow!("invalid qty={}", input.qty))
    }
}

#[surrealism]
fn get_item(id: RecordId) -> Result<Item> {
    let params = vec![("id".into(), Value::RecordId(id))];

    let mut res: Vec<Item> = surrealism::sql_with_vars("SELECT * FROM items WHERE id=$id", params)?;

    res.pop().ok_or(anyhow!("not found"))
}

#[surrealism]
fn check_qty(qty: i32) -> bool {
    qty >= 1
}
