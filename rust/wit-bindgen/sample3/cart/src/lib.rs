wit_bindgen::generate!("cart");

struct Component;

export!(Component);

impl Guest for Component {
    #[allow(async_fn_in_trait)]
    fn create(id: CartId) -> CartState {
        CartState::Empty(EmptyCart { id: id.clone() })
    }

    #[allow(async_fn_in_trait)]
    fn add_item(state: CartState, item: ItemId, qty: Quantity) -> Option<CartState> {
        if qty == 0 {
            return None;
        }

        find_price(&item).and_then(|p| {
            add_cart_item(
                state,
                CartItem {
                    item: item.clone(),
                    qty,
                    unit_price: p,
                },
            )
        })
    }
}

fn add_cart_item(state: CartState, citem: CartItem) -> Option<CartState> {
    match state {
        CartState::Empty(EmptyCart { id }) => Some(CartState::Active(ActiveCart {
            id: id.clone(),
            items: vec![citem],
        })),
        CartState::Active(ActiveCart { id, items }) => {
            let mut new_items = items.clone();
            new_items.push(citem);

            Some(CartState::Active(ActiveCart {
                id: id.clone(),
                items: new_items,
            }))
        }
    }
}
