use std::ops::Not;

pub type CartId = String;
pub type ItemId = String;
pub type Quantity = u32;

pub type OrderId = String;

#[allow(dead_code)]
#[derive(Debug, Clone)]
pub enum CartCommand {
    Create {
        cart_id: CartId,
    },
    Cancel {
        cart_id: CartId,
    },
    ChangeQty {
        cart_id: CartId,
        item_id: ItemId,
        qty: Quantity,
    },
    Order {
        cart_id: CartId,
        order_id: OrderId,
    },
}

#[allow(dead_code)]
#[derive(Debug, Clone)]
pub enum CartEvent {
    Created {
        cart_id: CartId,
    },
    Cancelled {
        cart_id: CartId,
    },
    ChangedQty {
        cart_id: CartId,
        item_id: ItemId,
        qty: Quantity,
    },
    Ordered {
        cart_id: CartId,
        order_id: OrderId,
    },
}

impl CartEvent {
    fn created(cart_id: CartId) -> Self {
        Self::Created {
            cart_id: cart_id,
        }
    }

    fn cancelled(cart_id: CartId) -> Self {
        Self::Cancelled {
            cart_id: cart_id,
        }
    }

    fn changed(cart_id: CartId, item_id: ItemId, qty: Quantity) -> Self {
        Self::ChangedQty {
            cart_id: cart_id,
            item_id: item_id,
            qty: qty,
        }
    }

    fn ordered(cart_id: CartId, order_id: OrderId) -> Self {
        Self::Ordered {
            cart_id: cart_id,
            order_id: order_id,
        }
    }
}

#[allow(dead_code)]
#[derive(Debug, Clone, PartialEq)]
pub enum Cart {
    Nothing,
    Empty {
        id: CartId,
    },
    Active {
        id: CartId,
        items: Vec<CartItem>,
    },
    Ordered {
        id: CartId,
        items: Vec<CartItem>,
        order_id: OrderId,
    },
    Cancelled {
        id: CartId,
    },
}

#[derive(Debug, Clone, PartialEq)]
pub struct CartItem {
    item_id: ItemId,
    qty: Quantity,
}

#[allow(dead_code)]
impl Cart {
    pub fn handle(self, cmd: &CartCommand) -> (Self, Option<CartEvent>) {
        match cmd {
            CartCommand::Create { cart_id } =>
                Self::transit(self, CartEvent::created(cart_id.clone())),
            CartCommand::Cancel { cart_id } =>
                Self::transit(self, CartEvent::cancelled(cart_id.clone())),
            CartCommand::ChangeQty { cart_id, item_id, qty } =>
                Self::transit(self, 
                    CartEvent::changed(cart_id.clone(), item_id.clone(), *qty)),
            CartCommand::Order { cart_id, order_id } =>
                Self::transit(self, 
                    CartEvent::ordered(cart_id.clone(), order_id.clone())),
        }
    }

    pub fn restore<'a, T>(self, events: T) -> Self
        where
            T: Iterator<Item = &'a CartEvent>
    {
        events.fold(self, Self::apply)
    }

    fn transit(self, event: CartEvent) -> (Self, Option<CartEvent>) {
        let state = Self::apply(self.clone(), &event);

        if self.eq(&state) {
            (self, None)
        } else {
            (state, Some(event))
        }
    }

    fn apply(self, event: &CartEvent) -> Self {
        match &self {
            Self::Nothing => match event {
                CartEvent::Created { cart_id } 
                    if cart_id.is_empty().not() =>
                        Self::Empty { id: cart_id.clone() },
                _ => self
            },
            Self::Empty { id: cid } => match event {
                CartEvent::ChangedQty { cart_id, item_id, qty} 
                    if cid.eq(cart_id) && *qty > 0 => {
                        let item = CartItem { 
                            item_id: item_id.clone(), 
                            qty: *qty
                        };

                        Self::Active { id: cart_id.clone(), items: vec![item] }
                    },
                CartEvent::Cancelled { cart_id } 
                    if cid.eq(cart_id) =>
                        Self::Cancelled { id: cart_id.clone() },
                _ => self
            },
            Self::Active { id: cid, items } => match event {
                CartEvent::ChangedQty { cart_id, item_id, qty} 
                    if cid.eq(cart_id) => 
                        Self::change_qty(items, cart_id, item_id, *qty),
                CartEvent::Cancelled { cart_id } 
                    if cid.eq(cart_id) =>
                        Self::Cancelled { id: cart_id.clone() },
                CartEvent::Ordered { cart_id, order_id }
                    if cid.eq(cart_id) =>
                        Self::Ordered {
                            id: cart_id.clone(), 
                            items: items.clone(), 
                            order_id: order_id.clone()
                        },
                _ => self
            },
            _ => self
        }
    }

    fn change_qty(items: &Vec<CartItem>, cart_id: &CartId, 
        item_id: &ItemId, qty: Quantity) -> Self {

        let item = 
            if qty > 0 {
                Some(CartItem { item_id: item_id.clone(), qty: qty })
            } else {
                None
            };

        let new_items: Vec<_> =
            items.iter()
                    .cloned()
                    .filter(|t| t.item_id.eq(item_id).not())
                    .chain(item.iter().cloned())
                    .collect();

        if new_items.is_empty() {
            Self::Empty { id: cart_id.clone() }
        } else {
            Self::Active { id: cart_id.clone(), items: new_items }
        }
    }
}
