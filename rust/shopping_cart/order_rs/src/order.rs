#![allow(dead_code)]

use rust_decimal::prelude::*;
use std::fmt::Debug;

pub type Quantity = usize;
pub type Amount = Decimal;

#[derive(Debug, Clone, PartialEq)]
pub struct NotBlankString {
    value: String,
}

impl NotBlankString {
    pub fn new(value: String) -> Option<Self> {
        let value = value.trim();

        if value.is_empty() {
            None
        } else {
            Some(Self {
                value: value.into(),
            })
        }
    }
}

pub type OrderId = NotBlankString;
pub type ItemId = NotBlankString;

#[derive(Debug, Clone, PartialEq)]
pub enum OrderEvent {
    None,
    Started(OrderId),
    ItemAdded {
        item_id: ItemId,
        unit_price: Amount,
    },
    ItemQtyChanged {
        item_id: ItemId,
        qty: Quantity,
        old_qty: Quantity,
    },
    ItemPriceChanged {
        item_id: ItemId,
        price: Amount,
        old_price: Amount,
    },
    CheckOut {
        order_total: Amount,
    },
}

#[derive(Debug, Clone)]
pub enum OrderAction {
    Start(OrderId),
    AddItem { item_id: ItemId, unit_price: Amount },
    ChangeQty { item_id: ItemId, qty: Quantity },
    ChangePrice { item_id: ItemId, price: Amount },
    Checkout,
}

#[derive(Debug, Clone, PartialEq)]
pub enum Order {
    None,
    Empty(OrderId),
    Shopping {
        id: OrderId,
        items: Vec<OrderItem>,
    },
    CheckOut {
        id: OrderId,
        items: Vec<OrderItem>,
        order_total: Amount,
    },
}

#[derive(Debug, Clone, PartialEq)]
pub struct OrderItem {
    item_id: ItemId,
    qty: Quantity,
    unit_price: Amount,
}

#[derive(Debug, Clone, PartialEq)]
pub enum OrderError {
    InvalidState,
    NegativePrice(Amount),
    ExistsItem(ItemId),
    NotFoundItem(ItemId),
    InvalidSubtotal(Amount),
}

type Result<T> = std::result::Result<T, OrderError>;

impl Order {
    pub fn action(&self, act: &OrderAction) -> Result<(Self, OrderEvent)> {
        match act {
            OrderAction::Start(id) => {
                if *self == Order::None {
                    let state = Order::Empty(id.clone());
                    let event = OrderEvent::Started(id.clone());

                    Ok((state, event))
                } else {
                    Err(OrderError::InvalidState)
                }
            }
            OrderAction::AddItem {
                item_id,
                unit_price,
            } => {
                if unit_price.is_sign_negative() {
                    return Err(OrderError::NegativePrice(*unit_price));
                }

                match self {
                    Self::Empty(id) => {
                        let item = OrderItem {
                            item_id: item_id.clone(),
                            qty: 1,
                            unit_price: *unit_price,
                        };

                        let state = Self::Shopping {
                            id: id.clone(),
                            items: vec![item],
                        };
                        let event = OrderEvent::ItemAdded {
                            item_id: item_id.clone(),
                            unit_price: *unit_price,
                        };

                        Ok((state, event))
                    }
                    Self::Shopping { id, items } => {
                        let exists = items.iter().find(|x| x.item_id == *item_id).is_some();

                        if exists {
                            return Err(OrderError::ExistsItem(item_id.clone()));
                        }

                        let item = OrderItem {
                            item_id: item_id.clone(),
                            qty: 1,
                            unit_price: *unit_price,
                        };

                        let new_items = [items.clone(), vec![item]].concat();

                        let state = Self::Shopping {
                            id: id.clone(),
                            items: new_items,
                        };
                        let event = OrderEvent::ItemAdded {
                            item_id: item_id.clone(),
                            unit_price: *unit_price,
                        };

                        Ok((state, event))
                    }
                    _ => Err(OrderError::InvalidState),
                }
            }
            OrderAction::ChangeQty { item_id, qty } => match self {
                Self::Shopping { id, items } => {
                    let exists = items.iter().find(|x| x.item_id == *item_id).is_some();

                    if !exists {
                        return Err(OrderError::NotFoundItem(item_id.clone()));
                    }

                    let mut old_qty = 0;

                    let new_items = items.iter().fold(vec![], |mut acc, x| {
                        if x.item_id == *item_id {
                            if *qty > 0 {
                                acc.push(OrderItem {
                                    item_id: x.item_id.clone(),
                                    qty: *qty,
                                    unit_price: x.unit_price.clone(),
                                });
                            }

                            old_qty = x.qty;
                        } else {
                            acc.push(x.clone());
                        }

                        acc
                    });

                    let event = if new_items == *items {
                        OrderEvent::None
                    } else {
                        OrderEvent::ItemQtyChanged {
                            item_id: item_id.clone(),
                            qty: *qty,
                            old_qty: old_qty,
                        }
                    };

                    if new_items.is_empty() {
                        Ok((Self::Empty(id.clone()), event))
                    } else {
                        Ok((
                            Self::Shopping {
                                id: id.clone(),
                                items: new_items,
                            },
                            event,
                        ))
                    }
                }
                _ => Err(OrderError::InvalidState),
            },
            OrderAction::ChangePrice { item_id, price } => match self {
                Self::Shopping { id, items } => {
                    let exists = items.iter().find(|x| x.item_id == *item_id).is_some();

                    if !exists {
                        return Err(OrderError::NotFoundItem(item_id.clone()));
                    } else if price.is_sign_negative() {
                        return Err(OrderError::NegativePrice(*price));
                    }

                    let mut event = OrderEvent::None;

                    let new_items = items
                        .iter()
                        .map(|x| {
                            if x.item_id == *item_id && x.unit_price != *price {
                                event = OrderEvent::ItemPriceChanged {
                                    item_id: x.item_id.clone(),
                                    price: *price,
                                    old_price: x.unit_price,
                                };

                                OrderItem {
                                    item_id: x.item_id.clone(),
                                    qty: x.qty,
                                    unit_price: *price,
                                }
                            } else {
                                x.clone()
                            }
                        })
                        .collect::<Vec<_>>();

                    Ok((
                        Self::Shopping {
                            id: id.clone(),
                            items: new_items,
                        },
                        event,
                    ))
                }
                _ => Err(OrderError::InvalidState),
            },
            OrderAction::Checkout => match self {
                Self::Shopping { id, items } => {
                    let subtotal = items.iter().fold(Amount::ZERO, |acc, x| acc + x.subtotal().unwrap_or(Amount::ZERO));

                    if subtotal >= Amount::ZERO {
                        Ok((
                            Self::CheckOut {
                                id: id.clone(),
                                items: items.clone(),
                                order_total: subtotal,
                            },
                            OrderEvent::CheckOut {
                                order_total: subtotal,
                            },
                        ))
                    } else {
                        Err(OrderError::InvalidSubtotal(subtotal))
                    }

                }
                _ => Err(OrderError::InvalidState),
            },
        }
    }
}

impl OrderItem {
    pub fn subtotal(&self) -> Option<Amount> {
        self.unit_price.checked_mul(self.qty.into())
    }
}

#[cfg(test)]
mod tests {
    use core::assert;

    use super::Order::*;
    use super::*;

    fn new_id(v: &str) -> NotBlankString {
        NotBlankString::new(v.into()).unwrap()
    }

    fn to_amount(v: isize) -> Amount {
        Amount::from_isize(v).unwrap()
    }

    #[test]
    fn new_notblankid() {
        let r = NotBlankString::new("id1".into());

        assert!(r.is_some());
        assert_eq!("id1", r.unwrap().value);
    }

    #[test]
    fn new_notblankid_with_blank() {
        let r = NotBlankString::new("  ".into());

        assert!(r.is_none());
    }

    #[test]
    fn none_start() {
        let id = new_id("order-1");
        let act = OrderAction::Start(id.clone());

        let r = None.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        assert_eq!(Order::Empty(id.clone()), st);
        assert_eq!(OrderEvent::Started(id.clone()), ev);
    }

    #[test]
    fn empty_start() {
        let state = Order::Empty(new_id("order-1"));
        let act = OrderAction::Start(new_id("order-0"));

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidState, r.unwrap_err());
    }

    #[test]
    fn add_item_to_empty() {
        let state = Order::Empty(new_id("order-1"));
        let price = Amount::from_usize(1000).unwrap();

        let act = OrderAction::AddItem {
            item_id: new_id("item-1"),
            unit_price: price,
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 1,
                unit_price: price,
            }],
        };

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::ItemAdded {
                item_id: new_id("item-1"),
                unit_price: price
            },
            ev
        );
    }

    #[test]
    fn add_item_to_none() {
        let state = Order::None;
        let price = Amount::from_usize(1000).unwrap();

        let act = OrderAction::AddItem {
            item_id: new_id("item-1"),
            unit_price: price,
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidState, r.unwrap_err());
    }

    #[test]
    fn add_item_to_none_with_minus_price() {
        let state = Order::Empty(new_id("order-1"));
        let price = Amount::from_isize(-100).unwrap();

        let act = OrderAction::AddItem {
            item_id: new_id("item-1"),
            unit_price: price,
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::NegativePrice(price), r.unwrap_err());
    }

    #[test]
    fn add_item_to_none_with_free_price() {
        let state = Order::Empty(new_id("order-1"));
        let price = Amount::from_usize(0).unwrap();

        let act = OrderAction::AddItem {
            item_id: new_id("item-1"),
            unit_price: price,
        };

        let r = state.action(&act);

        assert!(r.is_ok());
    }

    #[test]
    fn add_item_to_shopping() {
        let price1 = Amount::from_usize(500).unwrap();

        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 1,
                unit_price: price1,
            }],
        };

        let price2 = Amount::from_usize(1000).unwrap();

        let act = OrderAction::AddItem {
            item_id: new_id("item-2"),
            unit_price: price2,
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: price1,
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: price2,
                },
            ],
        };

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::ItemAdded {
                item_id: new_id("item-2"),
                unit_price: price2
            },
            ev
        );
    }

    #[test]
    fn add_same_item_to_shopping() {
        let price1 = Amount::from_usize(500).unwrap();

        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 1,
                unit_price: price1,
            }],
        };

        let price2 = Amount::from_usize(1000).unwrap();

        let act = OrderAction::AddItem {
            item_id: new_id("item-1"),
            unit_price: price2,
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::ExistsItem(new_id("item-1")), r.unwrap_err());
    }

    #[test]
    fn change_qty_to_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 1,
                unit_price: to_amount(1000),
            }],
        };

        let act = OrderAction::ChangeQty {
            item_id: new_id("item-1"),
            qty: 3,
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 3,
                unit_price: to_amount(1000),
            }],
        };

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::ItemQtyChanged {
                item_id: new_id("item-1"),
                qty: 3,
                old_qty: 1,
            },
            ev
        );
    }

    #[test]
    fn change_qty_to_multiitems_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 2,
                    unit_price: to_amount(2000),
                },
            ],
        };

        let act = OrderAction::ChangeQty {
            item_id: new_id("item-2"),
            qty: 5,
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 5,
                    unit_price: to_amount(2000),
                },
            ],
        };

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::ItemQtyChanged {
                item_id: new_id("item-2"),
                qty: 5,
                old_qty: 2,
            },
            ev
        );
    }

    #[test]
    fn change_qty_noitem_to_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 1,
                unit_price: to_amount(1000),
            }],
        };

        let act = OrderAction::ChangeQty {
            item_id: new_id("notfound-item-9"),
            qty: 3,
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(
            OrderError::NotFoundItem(new_id("notfound-item-9")),
            r.unwrap_err()
        );
    }

    #[test]
    fn change_zero_to_singleitem_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 3,
                unit_price: to_amount(1000),
            }],
        };

        let act = OrderAction::ChangeQty {
            item_id: new_id("item-1"),
            qty: 0,
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Empty(new_id("order-1"));

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::ItemQtyChanged {
                item_id: new_id("item-1"),
                qty: 0,
                old_qty: 3,
            },
            ev
        );
    }

    #[test]
    fn change_zero_to_multiitems_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: to_amount(2000),
                },
            ],
        };

        let act = OrderAction::ChangeQty {
            item_id: new_id("item-1"),
            qty: 0,
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-2"),
                qty: 1,
                unit_price: to_amount(2000),
            }],
        };

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::ItemQtyChanged {
                item_id: new_id("item-1"),
                qty: 0,
                old_qty: 1,
            },
            ev
        );
    }

    #[test]
    fn change_qty_to_empty() {
        let state = Order::Empty(new_id("order-1"));

        let act = OrderAction::ChangeQty {
            item_id: new_id("item-1"),
            qty: 3,
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidState, r.unwrap_err());
    }

    #[test]
    fn no_change_qty_to_multiitems_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: to_amount(2000),
                },
            ],
        };

        let act = OrderAction::ChangeQty {
            item_id: new_id("item-2"),
            qty: 1,
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: to_amount(2000),
                },
            ],
        };

        assert_eq!(expect, st);
        assert_eq!(OrderEvent::None, ev);
    }

    #[test]
    fn change_price_to_multiitems_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 2,
                    unit_price: to_amount(2000),
                },
            ],
        };

        let act = OrderAction::ChangePrice {
            item_id: new_id("item-2"),
            price: to_amount(2500),
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 2,
                    unit_price: to_amount(2500),
                },
            ],
        };

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::ItemPriceChanged {
                item_id: new_id("item-2"),
                price: to_amount(2500),
                old_price: to_amount(2000),
            },
            ev
        );
    }

    #[test]
    fn change_price_to_empty() {
        let state = Order::Empty(new_id("order-1"));

        let act = OrderAction::ChangePrice {
            item_id: new_id("item-1"),
            price: to_amount(100),
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidState, r.unwrap_err());
    }

    #[test]
    fn change_price_noitem_to_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 1,
                unit_price: to_amount(1000),
            }],
        };

        let act = OrderAction::ChangePrice {
            item_id: new_id("notfound-item-9"),
            price: to_amount(100),
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(
            OrderError::NotFoundItem(new_id("notfound-item-9")),
            r.unwrap_err()
        );
    }

    #[test]
    fn change_free_price() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 3,
                unit_price: to_amount(1000),
            }],
        };

        let act = OrderAction::ChangePrice {
            item_id: new_id("item-1"),
            price: to_amount(0),
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 3,
                unit_price: to_amount(0),
            }],
        };

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::ItemPriceChanged {
                item_id: new_id("item-1"),
                price: to_amount(0),
                old_price: to_amount(1000),
            },
            ev
        );
    }

    #[test]
    fn change_negative_price_to_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![OrderItem {
                item_id: new_id("item-1"),
                qty: 3,
                unit_price: to_amount(1000),
            }],
        };

        let act = OrderAction::ChangePrice {
            item_id: new_id("item-1"),
            price: to_amount(-100),
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::NegativePrice(to_amount(-100)), r.unwrap_err());
    }

    #[test]
    fn no_change_price_to_multiitems_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: to_amount(2000),
                },
            ],
        };

        let act = OrderAction::ChangePrice {
            item_id: new_id("item-2"),
            price: to_amount(2000),
        };

        let r = state.action(&act);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 1,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: to_amount(2000),
                },
            ],
        };

        assert_eq!(expect, st);
        assert_eq!(OrderEvent::None, ev);
    }

    #[test]
    fn change_negative_price_to_empty() {
        let state = Order::Empty(new_id("order-1"));

        let act = OrderAction::ChangePrice {
            item_id: new_id("item-1"),
            price: to_amount(-100),
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidState, r.unwrap_err());
    }

    #[test]
    fn change_price_to_none() {
        let state = Order::Empty(new_id("order-1"));

        let act = OrderAction::ChangePrice {
            item_id: new_id("item-1"),
            price: to_amount(100),
        };

        let r = state.action(&act);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidState, r.unwrap_err());
    }

    #[test]
    fn checkout_none() {
        let state = Order::None;

        let r = state.action(&OrderAction::Checkout);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidState, r.unwrap_err());
    }

    #[test]
    fn checkout_empty() {
        let state = Order::Empty(new_id("order-1"));

        let r = state.action(&OrderAction::Checkout);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidState, r.unwrap_err());
    }

    #[test]
    fn checkout_shopping() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 3,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: to_amount(2000),
                },
            ],
        };

        let r = state.action(&OrderAction::Checkout);

        assert!(r.is_ok());

        let (st, ev) = r.unwrap();

        let expect = Order::CheckOut {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 3,
                    unit_price: to_amount(1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: to_amount(2000),
                },
            ],
            order_total: to_amount(5000),
        };

        assert_eq!(expect, st);
        assert_eq!(
            OrderEvent::CheckOut {
                order_total: to_amount(5000)
            },
            ev
        );
    }

        #[test]
    fn checkout_shopping_negative_subtotal() {
        let state = Order::Shopping {
            id: new_id("order-1"),
            items: vec![
                OrderItem {
                    item_id: new_id("item-1"),
                    qty: 3,
                    unit_price: to_amount(-1000),
                },
                OrderItem {
                    item_id: new_id("item-2"),
                    qty: 1,
                    unit_price: to_amount(2000),
                },
            ],
        };

        let r = state.action(&OrderAction::Checkout);

        assert!(r.is_err());
        assert_eq!(OrderError::InvalidSubtotal(to_amount(-1000)), r.unwrap_err());
    }
}
