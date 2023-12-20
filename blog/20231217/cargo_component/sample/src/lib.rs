cargo_component_bindings::generate!();

use bindings::component::sample::types::{ActiveCart, CartItem, EmptyCart};
use bindings::{Cart, CartId, Guest, ItemId, Quantity};

struct Component;

impl Guest for Component {
    fn create(id: CartId) -> Cart {
        Cart::Empty(EmptyCart { id })
    }

    fn change_qty(state: Cart, item: ItemId, qty: Quantity) -> Cart {
        let (id, items) = match state {
            Cart::Empty(EmptyCart { ref id }) => (id.clone(), Vec::<CartItem>::new()),
            Cart::Active(ActiveCart { ref id, ref items }) => (id.clone(), items.clone()),
        };

        let new_items = update_items(items, item, qty);

        if new_items.is_empty() {
            Cart::Empty(EmptyCart { id })
        } else {
            Cart::Active(ActiveCart {
                id,
                items: new_items,
            })
        }
    }
}

fn update_items(mut items: Vec<CartItem>, item: ItemId, qty: Quantity) -> Vec<CartItem> {
    match items.iter().position(|v| v.item == item) {
        Some(index) => {
            if qty == 0 {
                items.remove(index);
            } else {
                items[index].qty = qty;
            }
        }
        None => {
            if qty > 0 {
                items.push(CartItem { item, qty });
            }
        }
    };

    items
}
