
pub type CartId = String;
pub type ItemCode = String;
pub type Quantity = u32;
pub type Amount = i32;

#[derive(Debug, Clone)]
pub enum Item {
    Product { code: ItemCode, price: Amount },
}

#[derive(Debug, Clone)]
pub struct CartLine {
    item: Item,
    qty: Quantity,
}

#[derive(Debug, Clone)]
pub enum Cart {
    Nothing,
    Empty { id: CartId },
    Active { id: CartId, lines: Vec<CartLine> },
}

#[derive(Debug, Clone)]
pub enum Event {
    Created { id: CartId },
    ItemAdded { item: Item, qty: Quantity },
    ItemRemoved { item: Item, qty: Quantity },
    QtyChanged { item: Item, new_qty: Quantity, old_qty: Quantity },
}

impl Item {
    pub fn code(&self) -> &ItemCode {
        match self {
            Self::Product { code, .. } => code
        }
    } 
}

type CartOutput = Option<(Cart, Event)>;

impl Cart {
    pub fn create(&self, id: CartId) -> CartOutput {
        match self {
            Self::Nothing => {
                let event = Event::Created { id: id.clone() };
                let state = Self::Empty { id: id.clone() };
                Some((state, event))
            }
            _ => None
        }        
    }

    pub fn change_qty<F>(&self, item_code: ItemCode, qty: Quantity, find: F) -> CartOutput 
        where
            F: Fn(&ItemCode) -> Option<Item>
    {
        match self {
            Self::Empty { id } if qty > 0 => {
                Self::find_item(&item_code, find)
                    .map(|item| {
                        let event = Event::ItemAdded { item: item.clone(), qty };

                        let state = Self::Active {
                            id: id.clone(), 
                            lines: vec![ CartLine { item, qty } ],
                        };

                        (state, event)
                    })
            }
            Self::Active { id, lines } => {
                let trg_line = lines
                    .iter()
                    .find(|&d| d.item_code() == &item_code);

                match trg_line {
                    Some(line) if qty == 0 => {
                        let event = Event::ItemRemoved { item: line.item.to_owned(), qty: line.qty };

                        let new_lines = lines
                            .iter()
                            .cloned()
                            .filter(|l| l.item_code().ne(&item_code))
                            .collect::<Vec<_>>();

                        if new_lines.is_empty() {
                            Some((Self::Empty { id: id.clone() }, event))
                        } else {
                            Some((Self::Active { id: id.clone(), lines: new_lines }, event))
                        }
                    }
                    Some(line) if qty != line.qty => {
                        let event = Event::QtyChanged { item: line.item.clone(), new_qty: qty, old_qty: line.qty };

                        let new_lines = lines
                            .iter()
                            .map(|l| 
                                if l.item_code() == line.item_code() {
                                    CartLine { qty, ..line.to_owned() }
                                } else {
                                    l.clone()
                                }
                            )
                            .collect::<Vec<_>>();

                        Some((Self::Active { id: id.clone(), lines: new_lines }, event))
                    }
                    None if qty > 0 => {
                        Self::find_item(&item_code, find)
                            .map(|item| {
                                let event = Event::ItemAdded { item: item.clone(), qty };

                                let state = Self::Active {
                                    id: id.clone(), 
                                    lines: [
                                        lines.clone(),
                                        vec![CartLine { item, qty }]
                                    ].concat()
                                };

                                (state, event)
                            })
                    }
                    _ => None
                }
            }
            _ => None
        }
    }

    fn find_item<F>(item_code: &ItemCode, find: F) -> Option<Item>
        where
            F: Fn(&ItemCode) -> Option<Item>
    {
        find(item_code).filter(|i| i.code() == item_code)
    }
}

impl CartLine {
    pub fn item_code(&self) -> &ItemCode {
        self.item.code()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn create_from_nothing() {
        if let Some((s, e)) = Cart::Nothing.create("cart-1".to_string()) {
            if let Cart::Empty { id } = s {
                assert_eq!(id, "cart-1".to_string());
            } else {
                assert!(false);
            }

            if let Event::Created { id } = e {
                assert_eq!(id, "cart-1".to_string());
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn create_from_other() {
        let state = Cart::Empty { id: "".to_string() };

        let r = state.create("cart-1".to_string());

        assert!(r.is_none());
    }

    #[test]
    fn add_item_to_empty() {
        let state = Cart::Empty { id: "cart-1".to_string() };
        let item1 = Item::Product { code: "item-1".to_string(), price: 1000 };

        if let Some((s, e)) = state.change_qty("item-1".to_string(), 2, |_| Some(item1.clone())) {
            if let Cart::Active { id, lines } = s {
                assert_eq!(id, "cart-1".to_string());

                let line = lines.first().unwrap();

                assert_eq!(line.item_code(), "item-1");
                assert_eq!(line.qty, 2);
            } else {
                assert!(false);
            }

            if let Event::ItemAdded { item, qty } = e {
                assert_eq!(item.code(), "item-1");
                assert_eq!(qty, 2);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_item_with_find_different_code() {
        let state = Cart::Empty { id: "cart-1".to_string() };
        let item1 = Item::Product { code: "item-1".to_string(), price: 1000 };

        let r = state.change_qty("item-2".to_string(), 2, |_| Some(item1.clone()));

        assert!(r.is_none());
    }

    #[test]
    fn add_zero_qty_to_empty() {
        let state = Cart::Empty { id: "cart-1".to_string() };
        let item1 = Item::Product { code: "item-1".to_string(), price: 1000 };

        let r = state.change_qty("item-1".to_string(), 0, |_| Some(item1.clone()));

        assert!(r.is_none());
    }

    #[test]
    fn add_item_to_active() {
        let state = Cart::Active { id: "cart-1".to_string(), lines: vec![
            CartLine { item: Item::Product { code: "item-1".to_string(), price: 100 }, qty: 2 }
        ] };

        let item2 = Item::Product { code: "item-2".to_string(), price: 200 };

        let r = state.change_qty("item-2".to_string(), 1, |_| Some(item2.clone()));

        if let Some((s, e)) = r {
            if let Cart::Active { id, lines } = s {
                assert_eq!(id, "cart-1".to_string());
                assert_eq!(lines.len(), 2);

                let line = lines.last().unwrap();

                assert_eq!(line.item_code(), "item-2");
                assert_eq!(line.qty, 1);
            } else {
                assert!(false);
            }

            if let Event::ItemAdded { item, qty } = e {
                assert_eq!(item.code(), "item-2");
                assert_eq!(qty, 1);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_qty() {
        let state = Cart::Active { id: "cart-1".to_string(), lines: vec![
            CartLine { item: Item::Product { code: "item-1".to_string(), price: 100 }, qty: 2 }
        ] };

        let item1 = Item::Product { code: "item-1".to_string(), price: 100 };

        let r = state.change_qty("item-1".to_string(), 1, |_| Some(item1.clone()));

        if let Some((s, e)) = r {
            if let Cart::Active { id, lines } = s {
                assert_eq!(id, "cart-1".to_string());
                assert_eq!(lines.len(), 1);

                let line = lines.first().unwrap();

                assert_eq!(line.item_code(), "item-1");
                assert_eq!(line.qty, 1);
            } else {
                assert!(false);
            }

            if let Event::QtyChanged { item, new_qty, old_qty } = e {
                assert_eq!(item.code(), "item-1");
                assert_eq!(new_qty, 1);
                assert_eq!(old_qty, 2);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_qty_with_same_qty() {
        let state = Cart::Active { id: "cart-1".to_string(), lines: vec![
            CartLine { item: Item::Product { code: "item-1".to_string(), price: 100 }, qty: 2 }
        ] };

        let item1 = Item::Product { code: "item-1".to_string(), price: 100 };

        let r = state.change_qty("item-1".to_string(), 2, |_| Some(item1.clone()));

        assert!(r.is_none());
    }

    #[test]
    fn remove_only_item() {
        let state = Cart::Active { id: "cart-1".to_string(), lines: vec![
            CartLine { item: Item::Product { code: "item-1".to_string(), price: 100 }, qty: 2 }
        ] };

        let item1 = Item::Product { code: "item-1".to_string(), price: 100 };

        let r = state.change_qty("item-1".to_string(), 0, |_| Some(item1.clone()));

        if let Some((s, e)) = r {
            if let Cart::Empty { id } = s {
                assert_eq!(id, "cart-1".to_string());
            } else {
                assert!(false);
            }

            if let Event::ItemRemoved { item, qty } = e {
                assert_eq!(item.code(), "item-1");
                assert_eq!(qty, 2);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn remove_item() {
        let state = Cart::Active { id: "cart-1".to_string(), lines: vec![
            CartLine { item: Item::Product { code: "item-1".to_string(), price: 100 }, qty: 2 },
            CartLine { item: Item::Product { code: "item-2".to_string(), price: 200 }, qty: 1 }
        ] };

        let item1 = Item::Product { code: "item-1".to_string(), price: 100 };

        let r = state.change_qty("item-1".to_string(), 0, |_| Some(item1.clone()));

        if let Some((s, e)) = r {
            if let Cart::Active { id, lines } = s {
                assert_eq!(id, "cart-1".to_string());
                assert_eq!(lines.len(), 1);

                let line = lines.first().unwrap();

                assert_eq!(line.item_code(), "item-2");
                assert_eq!(line.qty, 1);
            } else {
                assert!(false);
            }

            if let Event::ItemRemoved { item, qty } = e {
                assert_eq!(item.code(), "item-1");
                assert_eq!(qty, 2);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

}