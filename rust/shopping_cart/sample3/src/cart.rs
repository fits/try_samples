#![allow(dead_code)]

use super::amount::Value;
use super::price::Price;

pub type CartId = String;
pub type CartLineId = String;
pub type Quantity = u32;

#[derive(Debug, Clone)]
pub enum CartEvent<Item> {
    Created {
        id: CartId,
    },
    LineAdded {
        line_id: CartLineId,
        item: Item,
        qty: Quantity,
        unit_price: Price,
    },
    QtyChanged {
        line_id: CartLineId,
        new_qty: Quantity,
    },
    PriceChanged {
        line_id: CartLineId,
        new_price: Price,
    },
    Checkout,
}

#[derive(Debug, Clone, PartialEq)]
pub struct CartLine<Item> {
    line_id: CartLineId,
    item: Item,
    qty: Quantity,
    unit_price: Price,
}

#[derive(Debug, Clone, PartialEq)]
pub enum Cart<Item> {
    Nothing,
    Empty {
        id: CartId,
    },
    NonEmpty {
        id: CartId,
        lines: Vec<CartLine<Item>>,
    },
    Ordered {
        id: CartId,
        lines: Vec<CartLine<Item>>,
    },
}

type CartResult<Item> = (Cart<Item>, CartEvent<Item>);

impl<Item> Cart<Item>
where
    Item: Clone + PartialEq,
{
    pub fn apply_events(&self, events: Vec<&CartEvent<Item>>) -> Option<Self> {
        events.iter().fold(Some(self.clone()), |acc, x| {
            if let Some(state) = acc {
                match state {
                    Self::Nothing => match x {
                        CartEvent::Created { id } if !id.is_empty() => {
                            Some(Self::Empty { id: id.clone() })
                        }
                        _ => None,
                    },
                    Self::Empty { id } => Self::apply_to(&id, &vec![], x),
                    Self::NonEmpty { id, lines } => Self::apply_to(&id, &lines, x),
                    Self::Ordered { .. } => None,
                }
            } else {
                None
            }
        })
    }

    pub fn create(&self, id: CartId) -> Option<CartResult<Item>> {
        let event = CartEvent::Created { id };

        self.apply_events(vec![&event]).map(|s| (s, event))
    }

    pub fn add_line(
        &self,
        line_id: CartLineId,
        item: &Item,
        qty: Quantity,
        unit_price: Price,
    ) -> Option<CartResult<Item>> {
        let event = CartEvent::LineAdded {
            line_id,
            item: item.clone(),
            qty,
            unit_price,
        };

        self.apply_events(vec![&event]).map(|s| (s, event))
    }

    pub fn change_qty(&self, line_id: &CartLineId, qty: Quantity) -> Option<CartResult<Item>> {
        let event: CartEvent<Item> = CartEvent::QtyChanged {
            line_id: line_id.clone(),
            new_qty: qty,
        };

        self.apply_events(vec![&event]).map(|s| (s, event))
    }

    pub fn change_price(&self, line_id: &CartLineId, price: Price) -> Option<CartResult<Item>> {
        let event: CartEvent<Item> = CartEvent::PriceChanged {
            line_id: line_id.clone(),
            new_price: price,
        };

        self.apply_events(vec![&event]).map(|s| (s, event))
    }

    pub fn checkout(&self) -> Option<CartResult<Item>> {
        let event: CartEvent<Item> = CartEvent::Checkout;

        self.apply_events(vec![&event]).map(|s| (s, event))
    }

    fn validate_line(
        lines: &Vec<CartLine<Item>>,
        line_id: &CartLineId,
        qty: &Quantity,
        price: &Price,
    ) -> bool {
        !line_id.is_empty()
            && *qty > 0
            && Self::validate_price(price)
            && Self::not_exists_line(lines, line_id)
    }

    fn validate_price(price: &Price) -> bool {
        price.price_total() >= Value::default()
    }

    fn not_exists_line(lines: &Vec<CartLine<Item>>, line_id: &CartLineId) -> bool {
        lines.iter().find(|x| x.line_id == *line_id).is_none()
    }

    fn update_line<F: Fn(&CartLine<Item>) -> Option<Vec<CartLine<Item>>>>(
        id: &CartId,
        lines: &Vec<CartLine<Item>>,
        f: F,
    ) -> Option<Self> {
        let v: (bool, Vec<CartLine<Item>>) = (false, vec![]);

        let (upd, new_lines) = lines.iter().fold(v, |acc, x| {
            if let Some(cs) = f(x) {
                (true, [acc.1, cs].concat())
            } else {
                (acc.0, [acc.1, vec![x.clone()]].concat())
            }
        });

        if upd {
            if new_lines.is_empty() {
                Some(Self::Empty { id: id.clone() })
            } else {
                Some(Self::NonEmpty {
                    id: id.clone(),
                    lines: new_lines,
                })
            }
        } else {
            None
        }
    }

    fn apply_to(id: &CartId, lines: &Vec<CartLine<Item>>, event: &CartEvent<Item>) -> Option<Self> {
        match event {
            CartEvent::LineAdded {
                line_id,
                item,
                qty,
                unit_price,
            } if Self::validate_line(lines, line_id, qty, unit_price) => {
                let line = CartLine {
                    line_id: line_id.clone(),
                    item: item.clone(),
                    qty: *qty,
                    unit_price: unit_price.clone(),
                };

                let new_lines = [lines.clone(), vec![line]].concat();

                Some(Self::NonEmpty {
                    id: id.clone(),
                    lines: new_lines,
                })
            }
            CartEvent::QtyChanged { line_id, new_qty } => Self::update_line(id, lines, move |x| {
                if x.line_id == *line_id && x.qty != *new_qty {
                    if *new_qty > 0 {
                        Some(vec![CartLine {
                            qty: *new_qty,
                            ..x.clone()
                        }])
                    } else {
                        Some(vec![])
                    }
                } else {
                    None
                }
            }),
            CartEvent::PriceChanged { line_id, new_price } if Self::validate_price(new_price) => {
                Self::update_line(id, lines, move |x| {
                    if x.line_id == *line_id && x.unit_price != *new_price {
                        Some(vec![CartLine {
                            unit_price: new_price.clone(),
                            ..x.clone()
                        }])
                    } else {
                        None
                    }
                })
            }
            CartEvent::Checkout if !lines.is_empty() => Some(Self::Ordered {
                id: id.clone(),
                lines: lines.clone(),
            }),
            _ => None,
        }
    }
}

#[cfg(test)]
mod tests {
    use num_bigint::BigInt;
    use std::vec;

    use super::*;

    type LineInput = (&'static str, &'static str, Quantity, Price);

    fn empty_cart(id: &'static str) -> Cart<String> {
        Cart::Empty { id: id.to_string() }
    }

    fn single_line_cart(
        id: &'static str,
        line_id: &'static str,
        item: &'static str,
        qty: Quantity,
        unit_price: Price,
    ) -> Cart<String> {
        let line = CartLine {
            line_id: line_id.to_string(),
            item: item.to_string(),
            qty,
            unit_price,
        };

        Cart::NonEmpty {
            id: id.to_string(),
            lines: vec![line],
        }
    }

    fn multi_lines_cart(id: &'static str, lines: Vec<LineInput>) -> Cart<String> {
        let lines = lines
            .iter()
            .map(|(l, i, q, p)| CartLine {
                line_id: l.to_string(),
                item: i.to_string(),
                qty: *q,
                unit_price: p.clone(),
            })
            .collect::<Vec<_>>();

        Cart::NonEmpty {
            id: id.to_string(),
            lines,
        }
    }

    #[test]
    fn create() {
        let state: Cart<String> = Cart::Nothing;

        if let Some((s, ev)) = state.create("cart-1".to_string()) {
            if let Cart::Empty { id } = s {
                assert_eq!(id, "cart-1");
            } else {
                assert!(false);
            }

            if let CartEvent::Created { id } = ev {
                assert_eq!(id, "cart-1");
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn create_by_empty_id() {
        let state: Cart<String> = Cart::Nothing;

        assert!(state.create("".to_string()).is_none());
    }

    #[test]
    fn add_line_to_empty() {
        let state = empty_cart("cart-1");
        let item = "item-1".to_string();

        if let Some((s, _ev)) = state.add_line("line-1".to_string(), &item, 2, to_price(1000)) {
            if let Cart::NonEmpty { id, lines } = s {
                assert_eq!(id, "cart-1");
                assert_eq!(lines.len(), 1);

                let it = lines.last().unwrap();

                assert!(it.line_id.len() > 0);
                assert_eq!(it.item, "item-1");
                assert_eq!(it.qty, 2);
                assert_eq!(it.unit_price, to_price(1000));
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_line_to_empty_by_empty_id() {
        let state = empty_cart("cart-1");
        let item = "item-1".to_string();

        assert!(state
            .add_line("".to_string(), &item, 1, to_price(100))
            .is_none());
    }

    #[test]
    fn add_line_to_empty_with_zero_qty() {
        let state = empty_cart("cart-1");
        let item = "item-1".to_string();

        assert!(state
            .add_line("line-1".to_string(), &item, 0, to_price(100))
            .is_none());
    }

    #[test]
    fn add_other_item() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        let item2 = "item-2".to_string();

        if let Some((s, _ev)) = state.add_line("line-2".to_string(), &item2, 1, to_price(800)) {
            if let Cart::NonEmpty { id, lines } = s {
                assert_eq!(id, "cart-1");
                assert_eq!(lines.len(), 2);

                let it = lines.last().unwrap();

                assert!(it.line_id.len() > 0);
                assert_eq!(it.item, "item-2");
                assert_eq!(it.qty, 1);
                assert_eq!(it.unit_price, to_price(800));
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_same_item() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        let item = "item-1".to_string();

        if let Some((s, _ev)) = state.add_line("line-2".to_string(), &item, 1, to_price(800)) {
            if let Cart::NonEmpty { id, lines } = s {
                assert_eq!(id, "cart-1");
                assert_eq!(lines.len(), 2);

                let it = lines.last().unwrap();

                assert!(it.line_id.len() > 0);
                assert_eq!(it.item, "item-1");
                assert_eq!(it.qty, 1);
                assert_eq!(it.unit_price, to_price(800));
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_same_line_id() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        let item = "item-2".to_string();

        assert!(state
            .add_line("line-1".to_string(), &item, 1, to_price(800))
            .is_none());
    }

    #[test]
    fn apply_events_same_line_id_add() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        let event = CartEvent::LineAdded {
            line_id: "line-1".to_string(),
            item: "item-2".to_string(),
            qty: 1,
            unit_price: to_price(100),
        };

        assert!(state.apply_events(vec![&event]).is_none());
    }

    #[test]
    fn change_qty() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        if let Some((s, _ev)) = state.change_qty(&"line-1".to_string(), 1) {
            if let Cart::NonEmpty { id: _id, lines } = s {
                assert_eq!(lines.len(), 1);

                let it = lines.last().unwrap();

                assert_eq!(it.qty, 1);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_qty_zero() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        if let Some((s, _ev)) = state.change_qty(&"line-1".to_string(), 0) {
            if let Cart::Empty { id } = s {
                assert_eq!(id, "cart-1");
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_qty_invalid_line() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        assert!(state.change_qty(&"line-2".to_string(), 1).is_none());
    }

    #[test]
    fn change_qty_zero_with_lines() {
        let state = multi_lines_cart(
            "cart-1",
            vec![
                ("line-1", "item-1", 2, to_price(1500)),
                ("line-2", "item-2", 1, to_price(800)),
            ],
        );

        if let Some((s, _ev)) = state.change_qty(&"line-2".to_string(), 0) {
            if let Cart::NonEmpty { id: _id, lines } = s {
                assert_eq!(lines.len(), 1);

                let it = lines.last().unwrap();

                assert_eq!(it.line_id, "line-1");
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn no_change_qty() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        assert!(state.change_qty(&"line-1".to_string(), 2).is_none());
    }

    #[test]
    fn change_price() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        if let Some((s, _ev)) = state.change_price(&"line-1".to_string(), to_price(1000)) {
            if let Cart::NonEmpty { id: _id, lines } = s {
                assert_eq!(lines.len(), 1);

                let it = lines.last().unwrap();

                assert_eq!(it.unit_price, to_price(1000));
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_price_invalid_line() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        assert!(state
            .change_price(&"line-2".to_string(), to_price(100))
            .is_none());
    }

    #[test]
    fn no_change_price() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        assert!(state
            .change_price(&"line-1".to_string(), to_price(1500))
            .is_none());
    }

    #[test]
    fn checkout_empty() {
        let state = Cart::Empty::<String> {
            id: "cart-1".to_string(),
        };

        assert!(state.checkout().is_none());
    }

    #[test]
    fn checkout_non_empty() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        if let Some((s, _ev)) = state.checkout() {
            if let Cart::Ordered { id, lines } = s {
                assert_eq!(id, "cart-1");
                assert_eq!(lines.len(), 1);

                let it = lines.last().unwrap();

                assert_eq!(it.line_id, "line-1");
                assert_eq!(it.item, "item-1");
                assert_eq!(it.qty, 2);
                assert_eq!(it.unit_price, to_price(1500));
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn checkout_again() {
        let state = single_line_cart("cart-1", "line-1", "item-1", 2, to_price(1500));

        if let Some((s, _ev)) = state.checkout() {
            assert!(s.checkout().is_none());
        } else {
            assert!(false);
        }
    }

    fn to_price(v: i32) -> Price {
        let p = Value::from(BigInt::from(v));
        Price::new(p).unwrap()
    }
}
