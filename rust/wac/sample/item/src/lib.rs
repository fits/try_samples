use exports::sample::item::types::{Guest, ItemId, Price};

wit_bindgen::generate!("item");

struct Component;

impl Guest for Component {
    fn find_price(item: ItemId) -> Option<Price> {
        let v = (item.len() as i32) * 1000;
        Some(v)
    }
}

export!(Component);
