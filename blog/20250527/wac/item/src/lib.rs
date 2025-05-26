use exports::testapp::item::types::{Guest, Item, ItemId};

wit_bindgen::generate!("item");

struct Component;

impl Guest for Component {
    fn find(id: ItemId) -> Option<Item> {
        let price = (id.len() as i32) * 1000;
        Some(Item {
            id,
            unit_price: price,
        })
    }
}

export!(Component);
