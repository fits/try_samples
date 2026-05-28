use exports::sample1::cart::exports::{Cart, Guest, GuestCart};
use sample1::cart::imports::find_price;
use sample1::cart::types::*;

wit_bindgen::generate!({
    world: "root",
});

struct Host;

impl Guest for Host {
    type Cart = CartState;
}

export!(Host);

#[allow(dead_code)]
#[derive(Debug, Clone)]
struct CartState {
    id: CartId,
    lines: Vec<CartLine>,
}

impl GuestCart for CartState {
    #[allow(async_fn_in_trait)]
    fn new(id: CartId) -> Result<Self, Error>
    where
        Self: Sized,
    {
        let id = id.trim();

        if id.is_empty() {
            Err(Error::InvalidId(id.into()))
        } else {
            Ok(Self {
                id: id.into(),
                lines: vec![],
            })
        }
    }

    #[allow(async_fn_in_trait)]
    fn add_item(&self, item: ItemId, qty: Quantity) -> Result<Cart, Error> {
        let item = item.trim();

        if item.is_empty() {
            Err(Error::InvalidItem(item.into()))
        } else if qty == 0 {
            Err(Error::ZeroQty)
        } else {
            let unit_price = find_price(item).ok_or(Error::NotfoundItem(item.into()))?;

            let line = CartLine {
                item: item.into(),
                qty,
                unit_price,
            };

            let mut s = self.clone();
            s.lines.push(line);

            Ok(Cart::new(s))
        }
    }

    #[allow(async_fn_in_trait)]
    fn get_lines(&self) -> _rt::Vec<CartLine> {
        self.lines.clone()
    }
}
