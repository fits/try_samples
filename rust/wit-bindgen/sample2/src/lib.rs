
wit_bindgen::generate!("cart");

use types::{EmptyCart, ActiveCart, CartItem};

struct Component;

impl CartWorld for Component {
    fn create(id: CartId) -> Cart {
        Cart::EmptyCart(EmptyCart { id: id.clone() })
    }

    fn add_item(state: Cart, item: ItemIdResult, qty: Quantity) -> Option<Cart> {
        let price = find_price(&item);

        if let Some(p) = price {
            let ci = CartItem { item: item.clone(), qty, unit_price: p };

            match state {
                Cart::EmptyCart(EmptyCart { id }) => {
                    Some(Cart::ActiveCart(ActiveCart { id: id.clone(), items: vec![ci] }))
                }
                Cart::ActiveCart(ActiveCart { id, items }) => {
                    let new_items = [items.clone(), vec![ci]].concat();
                    Some(Cart::ActiveCart(ActiveCart { id: id.clone(), items: new_items }))
                }
            }
        } else {
            None
        }
    }
}

export_cart_world!(Component);
