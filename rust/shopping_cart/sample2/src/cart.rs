
pub type CartId = String;
pub type CatalogId = String;
pub type Quantity = u32;
pub type Amount = i32;

#[derive(Debug, Clone)]
pub struct Product {
    pub catalog_id: CatalogId,
    pub unit_price: Amount,
}

#[derive(Debug, Clone)]
pub struct CartItem {
    item: Product,
    qty: Quantity,
}

#[derive(Debug, Clone)]
pub enum Cart {
    Nothing,
    Empty { id: CartId },
    NonEmpty { id: CartId, items: Vec<CartItem> },
}

impl Cart {
    pub fn create(&self, id: CartId) -> Option<Self> {
        match self {
            Self::Nothing => Some(Self::Empty { id }),
            _ => None
        }
    }

    pub fn add_item(&self, item: Product, qty: Quantity) -> Option<Self> {
        if qty <= 0 {
            None
        } else {
            match self {
                Self::Empty { id } => {
                    let item = CartItem { item, qty };
                    Some(Self::NonEmpty { id: id.clone(), items: vec![item] })
                }
                Self::NonEmpty { id, items } => {
                    let mut upd = false;

                    let mut new_items = items.iter().map(|i| {
                        if i.item.catalog_id == item.catalog_id {
                            upd = true;
                            CartItem { item: i.item.clone(), qty: i.qty + qty }
                        } else {
                            i.clone()
                        }
                    }).collect::<Vec<_>>();

                    if !upd {
                        new_items.push(CartItem { item, qty });
                    }

                    Some(Self::NonEmpty { id: id.clone(), items: new_items })
                }
                Self::Nothing => None,
            }    
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn create() {
        let state = Cart::Nothing;

        if let Some(c) = state.create("cart-1".to_string()) {
            if let Cart::Empty { id } = c {
                assert_eq!(id, "cart-1");
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_item_to_empty() {
        let state = Cart::Empty { id: "cart-1".to_string() };
        let item = Product { catalog_id: "item-1".to_string(), unit_price: 1000 };

        if let Some(c) = state.add_item(item, 2) {
            if let Cart::NonEmpty { id, items } = c {
                assert_eq!(id, "cart-1");
                assert_eq!(items.len(), 1);

                let it = items.last().unwrap();

                assert_eq!(it.item.catalog_id, "item-1");
                assert_eq!(it.item.unit_price, 1000);
                assert_eq!(it.qty, 2);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_other_item() {
        let item = Product { catalog_id: "item-1".to_string(), unit_price: 1000 };
        let state = Cart::NonEmpty { id: "cart-1".to_string(), items: vec![CartItem { item, qty: 2 }] };

        let item2 = Product { catalog_id: "item-2".to_string(), unit_price: 2000 };

        if let Some(c) = state.add_item(item2, 1) {
            if let Cart::NonEmpty { id: _, items } = c {
                assert_eq!(items.len(), 2);

                let it = items.last().unwrap();

                assert_eq!(it.item.catalog_id, "item-2");
                assert_eq!(it.item.unit_price, 2000);
                assert_eq!(it.qty, 1);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_same_item() {
        let item = Product { catalog_id: "item-1".to_string(), unit_price: 1000 };
        let state = Cart::NonEmpty { id: "cart-1".to_string(), items: vec![CartItem { item: item.clone(), qty: 2 }] };

        if let Some(c) = state.add_item(item, 1) {
            if let Cart::NonEmpty { id: _, items } = c {
                assert_eq!(items.len(), 1);

                let it = items.last().unwrap();

                assert_eq!(it.item.catalog_id, "item-1");
                assert_eq!(it.qty, 3);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

}