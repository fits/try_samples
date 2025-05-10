use exports::sample::cart::types::{CartId, CartItem, CartState, Guest, ItemId, Quantity};
use sample::item::types::find_price;

wit_bindgen::generate!({
    world: "cart",
    with: {
        "sample:item/types": generate,
    }
});

struct Component;

impl Guest for Component {
    fn create(id: CartId) -> CartState {
        CartState::Empty(id)
    }

    fn add_item(state: CartState, item: ItemId, qty: Quantity) -> Option<CartState> {
        if qty == 0 {
            return None;
        }

        find_price(&item).and_then(|p| {
            let item = CartItem {
                item: item.clone(),
                qty,
                unit_price: p,
            };

            add_cart_item(state, item)
        })
    }
}

export!(Component);

fn add_cart_item(state: CartState, item: CartItem) -> Option<CartState> {
    match state {
        CartState::Empty(id) => Some(CartState::Active((id.clone(), vec![item]))),
        CartState::Active((id, items)) => {
            let new_items = insert_or_update(&items, item);
            Some(CartState::Active((id.clone(), new_items)))
        }
    }
}

fn insert_or_update(src: &Vec<CartItem>, item: CartItem) -> Vec<CartItem> {
    let mut res = vec![];
    let mut upd = false;

    for v in src {
        if v.item == item.item {
            res.push(CartItem {
                qty: v.qty + item.qty,
                ..v.clone()
            });
            upd = true;
        } else {
            res.push(v.clone());
        }
    }

    if !upd {
        res.push(item);
    }

    res
}
