use macuru::{MonadLike, adt, mdo};
use rust_decimal::prelude::*;

mod core;
use core::*;

adt!(
    CartState = EmptyCart | ActiveCart | CanceledCart derive Debug, Clone with CartFunc {
        fn add_item(&self, item: Item, qty: Quantity, from: Location) -> Result<Self>;
        fn remove_item(&self, item: Item, qty: Quantity) -> Result<Self>;
        fn items(&self) -> Vec<CartInItem>;
        fn cancel(&self) -> Result<Self>;
        fn subtotal(&self) -> Amount;
    }
);

#[derive(Debug, Clone)]
pub struct EmptyCart {
    cart: Cart,
    history: Movement,
}

#[derive(Debug, Clone)]
pub struct ActiveCart {
    cart: Cart,
    history: Movement,
    items: Vec<CartInItem>,
}

#[allow(unused)]
#[derive(Debug, Clone)]
pub struct CanceledCart {
    cart: Cart,
    history: Movement,
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

impl CartState {
    pub fn new(cart_id: CartId, user: Owner) -> Result<Self> {
        let cart = Cart::new(cart_id)?;

        let m = ChangeOwner::new(cart.clone().into(), System.into(), user)?;

        Ok(EmptyCart {
            cart,
            history: m.into(),
        }
        .into())
    }
}

fn invalid_state<T>() -> Result<T> {
    Err("invalid state".into())
}

impl CartFunc for EmptyCart {
    fn add_item(&self, item: Item, qty: Quantity, from: Location) -> Result<CartState> {
        let Self { cart, history } = self;

        let m: Movement =
            ChangeLocation::new((item, qty).try_into()?, from, cart.clone().into())?.into();

        let new_history: Movement = (history.clone(), m).into();
        let new_items = new_history.items_in_cart(&self.cart);

        if new_items.is_empty() {
            invalid_state()
        } else {
            Ok(ActiveCart {
                cart: cart.clone(),
                history: new_history,
                items: new_items,
            }
            .into())
        }
    }

    fn remove_item(&self, _item: Item, _qty: Quantity) -> Result<CartState> {
        invalid_state()
    }

    fn items(&self) -> Vec<CartInItem> {
        Vec::new()
    }

    fn cancel(&self) -> Result<CartState> {
        Ok(CanceledCart {
            cart: self.cart.clone(),
            history: self.history.clone(),
        }
        .into())
    }

    fn subtotal(&self) -> Amount {
        Amount::zero()
    }
}

impl CartFunc for ActiveCart {
    fn add_item(&self, item: Item, qty: Quantity, from: Location) -> Result<CartState> {
        let Self {
            cart,
            history,
            items,
        } = self;

        let m: Movement =
            ChangeLocation::new((item, qty).try_into()?, from, cart.clone().into())?.into();

        let new_history: Movement = (history.clone(), m).into();
        let new_items = new_history.items_in_cart(&self.cart);

        if *items == new_items {
            invalid_state()
        } else {
            Ok(Self {
                cart: cart.clone(),
                history: new_history,
                items: new_items,
            }
            .into())
        }
    }

    fn remove_item(&self, item: Item, qty: Quantity) -> Result<CartState> {
        if qty < 1 {
            Err(format!("must be qty >= 1, qty={}", qty).into())
        } else {
            let (remain_qty, new_history) = self
                .items
                .iter()
                .filter(|x| *x.item.item() == item && x.item.qty() > 0)
                .fold((qty, self.history.clone()), |acc, x| {
                    if acc.0 == 0 {
                        acc
                    } else {
                        let q = std::cmp::min(x.item.qty(), acc.0);

                        let m = mdo!(
                            t <- (item.clone(), q).try_into()
                            m <- ChangeLocation::new(
                                t,
                                self.cart.clone().into(),
                                x.from.clone(),
                            )
                            yield m
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
                let new_items = new_history.items_in_cart(&self.cart);

                if new_items.is_empty() {
                    Ok(EmptyCart {
                        cart: self.cart.clone(),
                        history: new_history,
                    }
                    .into())
                } else {
                    Ok(Self {
                        cart: self.cart.clone(),
                        history: new_history,
                        items: new_items,
                    }
                    .into())
                }
            }
        }
    }

    fn items(&self) -> Vec<CartInItem> {
        self.items.clone()
    }

    fn cancel(&self) -> Result<CartState> {
        let items = self.items.clone();

        let fst_item = items.first().ok_or("empty item")?;

        let mut m: Movement = move_out_item(fst_item, &self.cart)?;

        for x in items.iter().skip(1) {
            m = (m, move_out_item(x, &self.cart)?).into();
        }

        let new_history: Movement = (self.history.clone(), m).into();

        if new_history.items_in_cart(&self.cart).is_empty() {
            Ok(CanceledCart {
                cart: self.cart.clone(),
                history: new_history,
            }
            .into())
        } else {
            invalid_state()
        }
    }

    fn subtotal(&self) -> Amount {
        self.items.iter().fold(Amount::zero(), |acc, x| {
            acc + (x.item.item().unit_price * Amount::from_isize(x.item.qty()).unwrap_or_default())
        })
    }
}

impl CartFunc for CanceledCart {
    fn add_item(&self, _item: Item, _qty: Quantity, _from: Location) -> Result<CartState> {
        invalid_state()
    }

    fn remove_item(&self, _item: Item, _qty: Quantity) -> Result<CartState> {
        invalid_state()
    }

    fn items(&self) -> Vec<CartInItem> {
        Vec::new()
    }

    fn cancel(&self) -> Result<CartState> {
        invalid_state()
    }

    fn subtotal(&self) -> Amount {
        Amount::zero()
    }
}

fn move_out_item(t: &CartInItem, cart: &Cart) -> Result<Movement> {
    ChangeLocation::new(t.item.clone(), cart.clone().into(), t.from.clone()).map(Movement::from)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn new_state() {
        let r = CartState::new("cart-1".into(), Anonymous.into());

        assert!(r.is_ok());

        let r = r.unwrap();

        assert!(EmptyCart::try_from(r).is_ok());
    }

    #[test]
    fn add_item() {
        let s = CartState::new("cart-1".into(), Anonymous.into()).unwrap();

        let item = Item::new("A1".into(), dec!(100)).unwrap();
        let w = Warehouse::new_logical("stock-1".into()).unwrap();

        let r = s.add_item(item, 1, w.into());

        assert!(r.is_ok());

        let r = r.unwrap();

        assert!(ActiveCart::try_from(r).is_ok());
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
        assert_eq!(1, t.first().unwrap().item.qty());
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

        let r = r.unwrap();

        assert!(r.items().is_empty());
        assert!(EmptyCart::try_from(r).is_ok());
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
        assert_eq!(2, i1.item.qty());

        let i2 = r.last().unwrap();
        assert_eq!(3, i2.item.qty());
    }

    #[test]
    fn cancel_empty() {
        let s = CartState::new("cart-1".into(), Anonymous.into()).unwrap();

        let r = s.cancel();

        assert!(r.is_ok());

        let r = r.unwrap();

        assert!(CanceledCart::try_from(r).is_ok());
    }

    #[test]
    fn cancel_canceled() {
        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.cancel()
            yield b
        );

        let r = s.unwrap().cancel();

        assert!(r.is_err());
    }

    #[test]
    fn cancel_active() {
        let item1 = Item::new("A1".into(), dec!(100)).unwrap();
        let item2 = Item::new("B2".into(), dec!(200)).unwrap();

        let loc1 = Warehouse::new_logical("stock-1".into()).unwrap();
        let loc2 = Warehouse::new_logical("stock-2".into()).unwrap();

        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.add_item(item1.clone(), 1, loc1.clone().into())
            c <- b.add_item(item2.clone(), 3, loc1.clone().into())
            d <- c.add_item(item1.clone(), 2, loc1.clone().into())
            e <- d.add_item(item1.clone(), 1, loc2.clone().into())

            yield e
        )
        .unwrap();

        let r = s.cancel();

        assert!(r.is_ok());

        let r = r.unwrap();

        assert!(r.items().is_empty());

        let c = CanceledCart::try_from(r);

        assert!(c.is_ok());
    }

    #[test]
    fn subtotal_empty() {
        let s = CartState::new("cart-1".into(), Anonymous.into()).unwrap();

        assert_eq!(Amount::zero(), s.subtotal());
    }

    #[test]
    fn subtotal_canceled() {
        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.cancel()
            yield b
        );

        assert_eq!(Amount::zero(), s.unwrap().subtotal());
    }

    #[test]
    fn subtotal_active() {
        let item1 = Item::new("A1".into(), dec!(100)).unwrap();
        let item2 = Item::new("B2".into(), dec!(200)).unwrap();

        let loc1 = Warehouse::new_logical("stock-1".into()).unwrap();
        let loc2 = Warehouse::new_logical("stock-2".into()).unwrap();

        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.add_item(item1.clone(), 3, loc1.clone().into())
            c <- b.add_item(item2.clone(), 2, loc1.clone().into())
            d <- c.add_item(item1.clone(), 1, loc2.clone().into())
            e <- d.remove_item(item2.clone(), 1)
            f <- e.add_item(item1.clone(), 2, loc1.clone().into())
            g <- f.remove_item(item1.clone(), 3)

            yield g
        );

        assert_eq!(dec!(500), s.unwrap().subtotal());
    }
}
