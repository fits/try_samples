
wit_bindgen::generate!("cart");

use types::{EmptyCart, ActiveCart, CartItem};

struct Component;

export_cart_world!(Component);

fn add_cart_item(state: Cart, citem: CartItem) -> Option<Cart> {
    match state {
        Cart::EmptyCart(EmptyCart { id }) => {
            Some(Cart::ActiveCart(ActiveCart { id: id.clone(), items: vec![citem] }))
        }
        Cart::ActiveCart(ActiveCart { id, items }) => {
            let new_items = [items.clone(), vec![citem]].concat();
            Some(Cart::ActiveCart(ActiveCart { id: id.clone(), items: new_items }))
        }
    }
}

impl CartWorld for Component {
    fn create(id: CartId) -> Cart {
        Cart::EmptyCart(EmptyCart { id: id.clone() })
    }

    fn add_item(state: Cart, item: ItemIdResult, qty: Quantity) -> Option<Cart> {
        if qty == 0 {
            return None
        }

        find_price(&item)
            .and_then(|p|
                add_cart_item(state, CartItem { item: item.clone(), qty, unit_price: p })
            )
    }
}
