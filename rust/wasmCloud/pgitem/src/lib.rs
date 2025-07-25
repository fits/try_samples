use exports::example::pgitem::types::{Amount, Guest, ItemId};
use wasmcloud::postgres::query::{PgValue, query};

wit_bindgen::generate!({
    world: "item",
    generate_all,
});
struct Component;

const UPSERT_ITEM: &str = r#"
INSERT INTO items (id, price) VALUES ($1, $2)
    ON CONFLICT (id) DO UPDATE SET price = $2
"#;

const SELECT_PRICE: &str = r#"
SELECT price FROM items WHERE id = $1
"#;

impl Guest for Component {
    fn get_price(id: ItemId) -> Option<Amount> {
        let res = query(SELECT_PRICE, &[PgValue::Text(id)]);

        if let Ok(rs) = res
            && let Some(r) = rs.first()
            && let Some(c) = r.first()
        {
            match c.value {
                PgValue::Int4(v) => Some(v),
                _ => None,
            }
        } else {
            None
        }
    }

    fn set_price(id: ItemId, price: Amount) -> bool {
        if let Ok(_) = query(UPSERT_ITEM, &[PgValue::Text(id), PgValue::Int4(price)]) {
            true
        } else {
            false
        }
    }

    fn test() -> String {
        let r1 = Self::set_price("item1".into(), 123);
        let r2 = Self::get_price("item1".into());

        format!("1={}, 2={:?}", r1, r2)
    }
}

export!(Component);
