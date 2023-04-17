
use std::cmp;

pub type CartId = String;
pub type CatalogId = String;
pub type PromotionId = String;
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
    PromotingSales { id: CartId, items: Vec<CartItem>, promotions: Vec<SalesPromotion> },
    CheckOutReady { id: CartId, items: Vec<CartItem>, promotions: Vec<SalesPromotion>, subtotal: Amount },
}

#[derive(Debug, Clone)]
pub enum SalesPromotion {
    Discount { promotion_id: PromotionId, discount: Amount },
}

impl Cart {
    pub fn create(&self, id: CartId) -> Option<Self> {
        match self {
            Self::Nothing => Some(Self::Empty { id }),
            _ => None
        }
    }

    pub fn add_item(&self, item: Product, qty: Quantity) -> Option<Self> {
        if qty <= 0 || item.unit_price < 0 {
            None
        } else {
            match self {
                Self::Empty { id } => {
                    let item = CartItem { item, qty };
                    Some(Self::NonEmpty { id: id.clone(), items: vec![item] })
                }
                Self::NonEmpty { id, items } | 
                Self::CheckOutReady { id, items, .. } => {
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
                Self::Nothing | Self::PromotingSales { .. } => None,
            }    
        }
    }

    pub fn begin_promotion(&self) -> Option<Self> {
        match self {
            Self::NonEmpty { id, items } => {
                Some(Self::PromotingSales { id: id.clone(), items: items.clone(), promotions: vec![] })
            }
            _ => None
        }
    }

    pub fn add_promotion(&self, promotion: SalesPromotion) -> Option<Self> {
        match self {
            Self::PromotingSales { id, items, promotions } if promotion.discount() > 0 => {
                let promotions = [promotions.clone(), vec![promotion]].concat();

                if Self::amount_items(items) >= Self::amount_promotions(&promotions) {
                    Some(Self::PromotingSales { id: id.clone(), items: items.clone(), promotions })
                } else {
                    None
                }
            }
            _ => None
        }
    }

    pub fn end_promotion(&self) -> Option<Self> {
        match self {
            Self::PromotingSales { id, items, promotions } => {
                Some(Self::CheckOutReady { id: id.clone(), items: items.clone(), promotions: promotions.clone(), subtotal: self.subtotal() })
            }
            _ => None
        }
    }

    pub fn cancel_promotion(&self) -> Option<Self> {
        match self {
            Self::PromotingSales { id, items, .. } => {
                Some(Self::NonEmpty { id:id.clone(), items: items.clone() })
            }
            _ => None
        }
    }

    fn subtotal(&self) -> Amount {
        match self {
            Self::NonEmpty { items, .. } => {
                Self::amount_items(items)
            }
            Self::PromotingSales { items, promotions, .. } => {
                cmp::max(0, Self::amount_items(items) - Self::amount_promotions(promotions))
            }
            Self::CheckOutReady { subtotal, .. } => {
                subtotal.clone()
            }
            _ => 0
        }
    }

    fn amount_items(items: &Vec<CartItem>) -> Amount {
        items.iter().fold(0, |acc, i| acc + i.amount())
    }

    fn amount_promotions(promotions: &Vec<SalesPromotion>) -> Amount {
        promotions.iter().fold(0, |acc, p| acc + p.discount())
    }
}

impl CartItem {
    pub fn amount(&self) -> Amount {
        self.item.unit_price * self.qty.try_into().unwrap_or(0)
    }
}

impl SalesPromotion {
    pub fn discount(&self) -> Amount {
        match self {
            Self::Discount { discount, .. } => discount.clone(),
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

    #[test]
    fn add_promotion() {
        let item = Product { catalog_id: "item-1".to_string(), unit_price: 1000 };
        let state = Cart::PromotingSales { id: "cart-1".to_string(), items: vec![CartItem { item: item.clone(), qty: 2 }], promotions: vec![] };

        let sp = SalesPromotion::Discount { promotion_id: "d1".to_string(), discount: 50 };

        if let Some(c) = state.add_promotion(sp) {
            if let Cart::PromotingSales { promotions, .. } = c { 
                assert_eq!(promotions.len(), 1);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_promotion_over_single_discount() {
        let item = Product { catalog_id: "item-1".to_string(), unit_price: 1000 };
        let state = Cart::PromotingSales { id: "cart-1".to_string(), items: vec![CartItem { item: item.clone(), qty: 2 }], promotions: vec![] };

        let sp = SalesPromotion::Discount { promotion_id: "d1".to_string(), discount: 2001 };

        assert!(state.add_promotion(sp).is_none());
    }

    #[test]
    fn add_promotion_second() {
        let item = Product { catalog_id: "item-1".to_string(), unit_price: 1000 };
        let sp = SalesPromotion::Discount { promotion_id: "d1".to_string(), discount: 500 };

        let state = Cart::PromotingSales { id: "cart-1".to_string(), items: vec![CartItem { item: item.clone(), qty: 2 }], promotions: vec![sp] };

        let sp2 = SalesPromotion::Discount { promotion_id: "d2".to_string(), discount: 300 };

        if let Some(c) = state.add_promotion(sp2) {
            if let Cart::PromotingSales { promotions, .. } = c { 
                assert_eq!(promotions.len(), 2);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn add_promotion_over_discount() {
        let item = Product { catalog_id: "item-1".to_string(), unit_price: 1000 };
        let sp = SalesPromotion::Discount { promotion_id: "d1".to_string(), discount: 500 };

        let state = Cart::PromotingSales { id: "cart-1".to_string(), items: vec![CartItem { item: item.clone(), qty: 2 }], promotions: vec![sp] };

        let sp2 = SalesPromotion::Discount { promotion_id: "d1".to_string(), discount: 1501 };

        assert!(state.add_promotion(sp2).is_none());
    }

}