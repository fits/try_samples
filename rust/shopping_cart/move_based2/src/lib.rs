mod core;
use core::*;

#[derive(Debug, Clone)]
pub struct CartState {
    cart: Cart,
    history: Movement,
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

impl CartState {
    pub fn new(cart_id: CartId, user: Owner) -> Result<Self> {
        let cart = Cart::new(cart_id)?;

        let history = ChangeOwner::new(cart.clone().into(), System.into(), user, 1)?;

        Ok(Self {
            cart,
            history: history.into(),
        })
    }

    pub fn add_item(&self, item: Item, qty: Quantity, from: Location) -> Result<Self> {
        let Self { cart, history } = self;

        let m: Movement = ChangeLocation::new(item, from, cart.clone().into(), qty)?.into();

        let new_history: Movement = (history.clone(), m).into();

        Ok(Self {
            cart: cart.clone(),
            history: new_history,
        })
    }

    pub fn remove_item(&self, item: Item, qty: Quantity) -> Result<Self> {
        if qty < 1 {
            Err(format!("must be qty >= 1, qty={}", qty).into())
        } else {
            let items = self.items();

            let (remain_qty, new_history) = items
                .iter()
                .filter(|x| x.item == item && x.qty > 0)
                .fold((qty, self.history.clone()), |acc, x| {
                    if acc.0 == 0 {
                        acc
                    } else {
                        let q = std::cmp::min(x.qty, acc.0);
                        let m = ChangeLocation::new(
                            item.clone(),
                            self.cart.clone().into(),
                            x.from.clone(),
                            q,
                        );

                        if let Ok(m) = m {
                            (acc.0 - q, (acc.1, m.into()).into())
                        } else {
                            acc
                        }
                    }
                });

            if remain_qty > 0 {
                Err("failed remove item".into())
            } else {
                Ok(Self {
                    cart: self.cart.clone(),
                    history: new_history,
                })
            }
        }
    }

    pub fn items(&self) -> Vec<CartInItem> {
        self.history.items_in_cart(&self.cart)
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use macuru::{MonadLike, mdo};
    use rust_decimal::prelude::*;

    #[test]
    fn new_state() {
        let s = CartState::new("cart-1".into(), Anonymous.into());

        assert!(s.is_ok());
    }

    #[test]
    fn add_item() {
        let s = CartState::new("cart-1".into(), Anonymous.into()).unwrap();

        let item = Item::new("A1".into(), dec!(100)).unwrap();
        let w = Warehouse::new_logical("stock-1".into()).unwrap();

        let r = s.add_item(item, 1, w.into());

        assert!(r.is_ok());
    }

    #[test]
    fn remove_item() {
        let item1 = Item::new("A1".into(), dec!(100)).unwrap();
        let item2 = Item::new("B2".into(), dec!(200)).unwrap();

        let loc1 = Warehouse::new_logical("stock-1".into()).unwrap();
        let loc2 = Warehouse::new_logical("stock-2".into()).unwrap();

        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.add_item(item1.clone(), 3, loc1.clone().into())
            c <- b.add_item(item2.clone(), 2, loc1.clone().into())
            d <- c.add_item(item1.clone(), 1, loc2.clone().into())

            yield d
        )
        .unwrap();

        let r = s.remove_item(item1.clone(), 2);

        assert!(r.is_ok());

        let t = r.unwrap().items();
        assert_eq!(3, t.len());
        assert_eq!(1, t.first().unwrap().qty);
    }

    #[test]
    fn remove_item_over() {
        let item1 = Item::new("A1".into(), dec!(100)).unwrap();
        let item2 = Item::new("B2".into(), dec!(200)).unwrap();

        let loc1 = Warehouse::new_logical("stock-1".into()).unwrap();
        let loc2 = Warehouse::new_logical("stock-2".into()).unwrap();

        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.add_item(item1.clone(), 3, loc1.clone().into())
            c <- b.add_item(item2.clone(), 2, loc1.clone().into())
            d <- c.add_item(item1.clone(), 1, loc2.clone().into())

            yield d
        )
        .unwrap();

        let r = s.remove_item(item1.clone(), 5);

        assert!(r.is_err());
    }

    #[test]
    fn remove_item_other_locations() {
        let item1 = Item::new("A1".into(), dec!(100)).unwrap();

        let loc1 = Warehouse::new_logical("stock-1".into()).unwrap();
        let loc2 = Warehouse::new_logical("stock-2".into()).unwrap();
        let loc3 = Warehouse::new_logical("stock-3".into()).unwrap();

        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.add_item(item1.clone(), 1, loc1.clone().into())
            c <- b.add_item(item1.clone(), 1, loc2.clone().into())
            d <- c.add_item(item1.clone(), 1, loc3.clone().into())

            yield d
        )
        .unwrap();

        let r = s.remove_item(item1.clone(), 3);

        assert!(r.is_ok());
        assert!(r.unwrap().items().is_empty());
    }

    #[test]
    fn remove_item_zero() {
        let item1 = Item::new("A1".into(), dec!(100)).unwrap();

        let loc1 = Warehouse::new_logical("stock-1".into()).unwrap();
        let loc2 = Warehouse::new_logical("stock-2".into()).unwrap();
        let loc3 = Warehouse::new_logical("stock-3".into()).unwrap();

        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.add_item(item1.clone(), 1, loc1.clone().into())
            c <- b.add_item(item1.clone(), 1, loc2.clone().into())
            d <- c.add_item(item1.clone(), 1, loc3.clone().into())

            yield d
        )
        .unwrap();

        let r = s.remove_item(item1.clone(), 0);

        assert!(r.is_err());
    }

    #[test]
    fn items() {
        let item1 = Item::new("A1".into(), dec!(100)).unwrap();
        let item2 = Item::new("B2".into(), dec!(200)).unwrap();

        let loc1 = Warehouse::new_logical("stock-1".into()).unwrap();

        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.add_item(item1.clone(), 1, loc1.clone().into())
            c <- b.add_item(item2.clone(), 3, loc1.clone().into())
            d <- c.add_item(item1.clone(), 1, loc1.clone().into())

            yield d
        )
        .unwrap();

        let r = s.items();

        assert_eq!(2, r.len());

        let i1 = r.first().unwrap();
        assert_eq!(2, i1.qty);

        let i2 = r.last().unwrap();
        assert_eq!(3, i2.qty);
    }
}
