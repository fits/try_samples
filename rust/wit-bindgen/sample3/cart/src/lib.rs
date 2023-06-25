
wit_bindgen::generate!("cart");

struct Component;

export_cart!(Component);

impl Cart for Component {
    fn create(id: CartId) -> CartData {
        CartData::EmptyCart(EmptyCart { id: id.clone() })
    }

    fn add_item(state: CartData, item: ItemId, qty: Quantity) -> Option<CartData> {
        if qty == 0 {
            return None
        }

        find_price(&item)
            .and_then(|p|
                add_cart_item(state, CartItem { item: item.clone(), qty, unit_price: p })
            )
    }
}

fn add_cart_item(state: CartData, citem: CartItem) -> Option<CartData> {
    match state {
        CartData::EmptyCart(EmptyCart { id }) => {
            Some(CartData::ActiveCart(ActiveCart { id: id.clone(), items: vec![citem] }))
        }
        CartData::ActiveCart(ActiveCart { id, items }) => {
            let new_items = insert_or_update(&items, citem);
            Some(CartData::ActiveCart(ActiveCart { id: id.clone(), items: new_items }))
        }
    }
}

fn insert_or_update(src: &Vec<CartItem>, citem: CartItem) -> Vec<CartItem> {
    let mut res = vec![];
    let mut upd = false;

    for v in src {
        if v.item == citem.item {
            res.push(CartItem { qty: v.qty + citem.qty, ..v.clone() });
            upd = true;
        } else {
            res.push(v.clone());
        }
    }

    if !upd {
        res.push(citem);
    }

    res
}
