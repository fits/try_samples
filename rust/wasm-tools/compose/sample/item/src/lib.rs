#[allow(warnings)]
mod bindings;

use bindings::exports::component::item::itemfind::Guest;
use bindings::exports::component::item::itemfind::{Amount, Item, ItemId};

struct Component;

impl Guest for Component {
    fn find_item(id: ItemId) -> Option<Item> {
        let price: Amount = 123;
        let item = Item { id, price };

        Some(item)
    }
}

bindings::export!(Component with_types_in bindings);
