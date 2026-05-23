use crate::resource_test::order::types::*;
use crate::resource_test::order::imports::now_datestring;
use crate::exports::resource_test::order::exports::{Guest, GuestOrder, Order};

wit_bindgen::generate!({
    world: "root",
});

struct Host;

impl Guest for Host {
    type Order = OrderState;
}

#[allow(dead_code)]
#[derive(Debug, Clone)]
struct OrderState {
    id: OrderId,
    lines: Vec<OrderedItem>,
}

impl GuestOrder for OrderState {
    #[allow(async_fn_in_trait)]
    fn new(id: OrderId) -> Result<Self, Error>
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
    fn order_item(&self, item: ItemId, qty: Quantity) -> Result<Order, Error> {
        let item = item.trim();

        if item.is_empty() {
            Err(Error::InvalidItem(item.into()))
        } else if qty == 0 {
            Err(Error::ZeroOrder)
        } else {
            let r = OrderedItem {
                item: item.into(),
                qty,
                at: now_datestring(),
            };

            let mut state = self.clone();
            state.lines.push(r);

            Ok(Order::new(state))
        }
    }

    #[allow(async_fn_in_trait)]
    fn ordered_items(&self) -> _rt::Vec<OrderedItem> {
        self.lines.clone()
    }
}
