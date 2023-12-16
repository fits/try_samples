cargo_component_bindings::generate!();

use bindings::component::sample::types::{ActiveCart, CartItem, EmptyCart};
use bindings::{Cart, CartId, Guest, ItemId, Quantity};

struct Component;

impl Guest for Component {
    fn create(id: CartId) -> Cart {
        Cart::Empty(EmptyCart { id })
    }

    fn change_qty(state: Cart, item: ItemId, qty: Quantity) -> Cart {
        match state {
            Cart::Empty(EmptyCart { id }) => Cart::Active(ActiveCart {
                id: id.clone(),
                items: vec![CartItem {
                    item: item.clone(),
                    qty,
                }],
            }),
            Cart::Active(ActiveCart { id, items }) => {
                let new_items = update_items(&items, item, qty);

                if new_items.is_empty() {
                    Cart::Empty(EmptyCart { id: id.clone() })
                } else {
                    Cart::Active(ActiveCart {
                        id: id.clone(),
                        items: new_items,
                    })
                }
            }
        }
    }
}

fn update_items(items: &Vec<CartItem>, item: ItemId, qty: Quantity) -> Vec<CartItem> {
    let mut res = vec![];
    let mut upd = false;

    for v in items {
        if v.item == item {
            upd = true;

            if qty > 0 {
                res.push(CartItem {
                    item: item.clone(),
                    qty,
                });
            }
        } else {
            res.push(v.clone());
        }
    }

    if !upd && qty > 0 {
        res.push(CartItem {
            item: item.clone(),
            qty,
        })
    }

    res
}
