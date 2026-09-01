use macuru::adt;

mod core;
pub use core::*;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

adt!(
    CartState = EmptyCart | ActiveCart | CanceledCart derive Debug, Clone with CartStateFunc {
        fn add_item(&self, product: Product, from: Location) -> Result<Self>;
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
}

#[derive(Debug, Clone)]
pub struct CanceledCart;

impl CartState {
    pub fn new(id: CartId, owner: Owner) -> Result<Self> {
        let cart = Cart::new(id)?;
        let target = MoveResource::new(cart.clone().into(), System.into(), None);

        let history = Movement::new_with_owner(target, owner, None)?;

        Ok(EmptyCart { cart, history }.into())
    }
}

impl CartStateFunc for EmptyCart {
    fn add_item(&self, product: Product, from: Location) -> Result<CartState> {
        let target = MoveResource::new(product.into(), System.into(), Some(from));
        let m = Movement::new_with_location(target, self.cart.clone().into(), None)?;

        Ok(ActiveCart {
            cart: self.cart.clone(),
            history: self.history.clone().cons(m),
        }
        .into())
    }
}

impl CartStateFunc for ActiveCart {
    fn add_item(&self, product: Product, from: Location) -> Result<CartState> {
        let target = MoveResource::new(product.into(), System.into(), Some(from));
        let m = Movement::new_with_location(target, self.cart.clone().into(), None)?;

        Ok(Self {
            cart: self.cart.clone(),
            history: self.history.clone().cons(m),
        }
        .into())
    }
}

impl CartStateFunc for CanceledCart {
    fn add_item(&self, _product: Product, _from: Location) -> Result<CartState> {
        Err("cart canceled".into())
    }
}

adt!(
    OrderState = Checkout | Paying | Confirmed derive Debug, Clone with OrderStateFunc {
        fn balance(&self) -> Amount;
        fn add_payment(&self, payment: Payment) -> Result<Self>;
        fn confirm(&self) -> Result<Self>;
        fn delivery(&self, address: Address, fee: Option<Amount>) -> Result<Self>;
    }
);

#[derive(Debug, Clone)]
pub struct Checkout {
    order: Order,
    history: Movement,
    delivery: Option<Movement>,
}

#[derive(Debug, Clone)]
pub struct Paying {
    order: Order,
    history: Movement,
    payments: Movement,
}

#[derive(Debug, Clone)]
pub struct Confirmed {
    order: Order,
    history: Movement,
}

impl OrderState {
    pub fn new(id: OrderId, cart: CartState, owner: Owner) -> Result<Self> {
        if let Ok(c) = ActiveCart::try_from(cart) {
            let order = Order::new(id)?;

            let mut order_history = Movement::new_with_owner(
                MoveResource::new(order.clone().into(), System.into(), None),
                owner,
                None,
            )?;

            for t in c.history.latest_target()? {
                if let Some(l) = t.location()
                    && l.eq_cart(&c.cart)
                {
                    let cost = t.resource().subtotal();
                    let m = Movement::new_with_location(t, order.clone().into(), cost)?;

                    order_history = order_history.cons(m);
                }
            }

            Ok(Checkout {
                order,
                history: c.history.clone().cons(order_history),
                delivery: None,
            }
            .into())
        } else {
            Err("can not create order".into())
        }
    }
}

impl OrderStateFunc for Checkout {
    fn balance(&self) -> Amount {
        let loc: Location = self.order.clone().into();

        let mut res = self.history.calc_cost(&loc).unwrap_or_default();

        if let Some(x) = &self.delivery {
            res += x.calc_cost(&loc).unwrap_or_default();
        }

        res
    }

    fn add_payment(&self, payment: Payment) -> Result<OrderState> {
        let balance = self.balance();
        let cost = payment.amount_billed() * -1;

        if balance + cost < 0 {
            Err("over payment".into())
        } else {
            let target = MoveResource::new(payment.into(), System.into(), Some(Unknown.into()));
            let m = Movement::new_with_location(target, self.order.clone().into(), Some(cost))?;

            let mut history = self.history.clone();

            if let Some(x) = &self.delivery {
                history = history.cons(x.clone());
            }

            Ok(Paying {
                order: self.order.clone(),
                history,
                payments: m,
            }
            .into())
        }
    }

    fn confirm(&self) -> Result<OrderState> {
        let balance = self.balance();

        if balance != 0 {
            Err(format!("balance is not zero. balance={}", balance).into())
        } else {
            Ok(Confirmed {
                order: self.order.clone(),
                history: self.history.clone(),
            }
            .into())
        }
    }

    fn delivery(&self, address: Address, fee: Option<Amount>) -> Result<OrderState> {
        if let Some(x) = fee
            && x <= 0
        {
            Err(format!("invalid shipping fee, fee={}", x).into())
        } else {
            let loc: Location = self.order.clone().into();

            let items = self
                .history
                .latest_target()?
                .iter()
                .filter_map(|x| {
                    if x.location() == Some(&loc) {
                        Product::try_from(x.resource().clone()).ok()
                    } else {
                        None
                    }
                })
                .collect::<Vec<_>>();

            let target = MoveResource::new(
                Product::new_bundle(None, items)?.into(),
                System.into(),
                loc.clone().into(),
            );

            let mut delivery = Movement::new_with_location(target, address.into(), None)?.promise();

            if let Some(x) = fee {
                let ship_fee = MoveResource::new(
                    ShippingFee::new(x)?.into(),
                    System.into(),
                    Some(Unknown.into()),
                );

                let m = Movement::new_with_location(ship_fee, loc.into(), Some(x))?;

                delivery = delivery.exchange(m);
            }

            Ok(Self {
                delivery: Some(delivery),
                ..self.clone()
            }
            .into())
        }
    }
}

impl OrderStateFunc for Paying {
    fn balance(&self) -> Amount {
        let loc: Location = self.order.clone().into();

        self.history.calc_cost(&loc).unwrap_or_default()
            + self.payments.calc_cost(&loc).unwrap_or_default()
    }

    fn add_payment(&self, payment: Payment) -> Result<OrderState> {
        let balance = self.balance();
        let cost = payment.amount_billed() * -1;

        if balance + cost < 0 {
            Err("over payment".into())
        } else {
            let target = MoveResource::new(payment.into(), System.into(), Some(Unknown.into()));
            let m = Movement::new_with_location(target, self.order.clone().into(), Some(cost))?;

            Ok(Self {
                payments: self.payments.clone().cons(m),
                ..self.clone()
            }
            .into())
        }
    }

    fn confirm(&self) -> Result<OrderState> {
        let balance = self.balance();

        if balance != 0 {
            Err(format!("balance is not zero. balance={}", balance).into())
        } else {
            Ok(Confirmed {
                order: self.order.clone(),
                history: self.history.clone().cons(self.payments.clone()),
            }
            .into())
        }
    }

    fn delivery(&self, _address: Address, _fee: Option<Amount>) -> Result<OrderState> {
        Err("can not change delivery to paying order".into())
    }
}

impl OrderStateFunc for Confirmed {
    fn balance(&self) -> Amount {
        0
    }

    fn add_payment(&self, _payment: Payment) -> Result<OrderState> {
        Err("can not add payment to the confirmed order".into())
    }

    fn confirm(&self) -> Result<OrderState> {
        Err("can not confirm the confirmed order".into())
    }

    fn delivery(&self, _address: Address, _fee: Option<Amount>) -> Result<OrderState> {
        Err("can not change delivery to confirmed order".into())
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use macuru::{MonadLike, mdo};

    #[test]
    fn cart_new() {
        let r = CartState::new("cart-1".into(), Anonymous.into());
        assert!(r.is_ok())
    }

    #[test]
    fn cart_add_item() {
        let s = CartState::new("cart-1".into(), Anonymous.into()).unwrap();

        let p = Product::new_single("A-item".into(), 500, 2).unwrap();
        let w = Warehouse::new_logical("stock-1".into()).unwrap();

        let r = s.add_item(p, w.into());

        assert!(r.is_ok());
    }

    #[test]
    fn order_new() {
        let cart = mdo!(
            p1 <- Product::new_single("A-item".into(), 1000, 2)
            p2 <- Product::new_single("B-item".into(), 2300, 3)
            p3 <- Product::new_single("C-item".into(), 450, 1)
            w1 <- Warehouse::new_logical("stock-1".into())

            c1 <- CartState::new("cart-1".into(), Anonymous.into())
            c2 <- c1.add_item(p1.clone(), w1.clone().into())
            c3 <- c2.add_item(p2.clone(), w1.clone().into())
            c4 <- c3.add_item(p3.clone(), w1.clone().into())
            yield c4
        )
        .unwrap();

        let r = OrderState::new("order-1".into(), cart, Anonymous.into());

        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(9350, r.balance());
    }

    #[test]
    fn order_new_with_empty() {
        let cart = CartState::new("cart-1".into(), Anonymous.into()).unwrap();

        let r = OrderState::new("order-1".into(), cart, Anonymous.into());

        assert!(r.is_err())
    }

    #[test]
    fn order_new_with_canceled() {
        let cart = CanceledCart.into();

        let r = OrderState::new("order-1".into(), cart, Anonymous.into());

        assert!(r.is_err())
    }

    #[test]
    fn partial_payment() {
        let o1 = mdo!(
            p1 <- Product::new_single("A-item".into(), 1000, 2)
            p2 <- Product::new_single("B-item".into(), 2300, 3)
            p3 <- Product::new_single("C-item".into(), 450, 1)
            w1 <- Warehouse::new_logical("stock-1".into())

            c1 <- CartState::new("cart-1".into(), Anonymous.into())
            c2 <- c1.add_item(p1.clone(), w1.clone().into())
            c3 <- c2.add_item(p2.clone(), w1.clone().into())
            c4 <- c3.add_item(p3.clone(), w1.clone().into())

            o1 <- OrderState::new("order-1".into(), c4, Anonymous.into())

            yield o1
        )
        .unwrap();

        let p = Payment::new_credit("123".into(), 5000).unwrap();
        let r = o1.add_payment(p);

        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(4350, r.balance());
    }

    #[test]
    fn over_payment() {
        let o1 = mdo!(
            p1 <- Product::new_single("A-item".into(), 1000, 2)
            p2 <- Product::new_single("B-item".into(), 2300, 3)
            p3 <- Product::new_single("C-item".into(), 450, 1)
            w1 <- Warehouse::new_logical("stock-1".into())

            c1 <- CartState::new("cart-1".into(), Anonymous.into())
            c2 <- c1.add_item(p1.clone(), w1.clone().into())
            c3 <- c2.add_item(p2.clone(), w1.clone().into())
            c4 <- c3.add_item(p3.clone(), w1.clone().into())

            o1 <- OrderState::new("order-1".into(), c4, Anonymous.into())

            yield o1
        )
        .unwrap();

        let p = Payment::new_credit("123".into(), 10000).unwrap();
        let r = o1.add_payment(p);

        assert!(r.is_err());
    }

    #[test]
    fn confirm() {
        let o1 = mdo!(
            p1 <- Product::new_single("A-item".into(), 1000, 2)
            p2 <- Product::new_single("B-item".into(), 2300, 3)
            p3 <- Product::new_single("C-item".into(), 450, 1)
            w1 <- Warehouse::new_logical("stock-1".into())
            py1 <- Payment::new_credit("PAY-1".into(), 5000)
            py2 <- Payment::new_credit("PAY-2".into(), 4350)

            c1 <- CartState::new("cart-1".into(), Anonymous.into())
            c2 <- c1.add_item(p1.clone(), w1.clone().into())
            c3 <- c2.add_item(p2.clone(), w1.clone().into())
            c4 <- c3.add_item(p3.clone(), w1.clone().into())

            o1 <- OrderState::new("order-1".into(), c4, Anonymous.into())
            o2 <- o1.add_payment(py1.clone())
            o3 <- o2.add_payment(py2.clone())

            yield o3
        )
        .unwrap();

        let r = o1.confirm();

        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(0, r.balance());
    }

    #[test]
    fn confirm_remain() {
        let o1 = mdo!(
            p1 <- Product::new_single("A-item".into(), 1000, 2)
            p2 <- Product::new_single("B-item".into(), 2300, 3)
            p3 <- Product::new_single("C-item".into(), 450, 1)
            w1 <- Warehouse::new_logical("stock-1".into())
            py1 <- Payment::new_credit("PAY-1".into(), 5000)
            py2 <- Payment::new_credit("PAY-2".into(), 2350)

            c1 <- CartState::new("cart-1".into(), Anonymous.into())
            c2 <- c1.add_item(p1.clone(), w1.clone().into())
            c3 <- c2.add_item(p2.clone(), w1.clone().into())
            c4 <- c3.add_item(p3.clone(), w1.clone().into())

            o1 <- OrderState::new("order-1".into(), c4, Anonymous.into())
            o2 <- o1.add_payment(py1.clone())
            o3 <- o2.add_payment(py2.clone())

            yield o3
        )
        .unwrap();

        let r = o1.confirm();

        assert!(r.is_err());
    }

    #[test]
    fn delivery() {
        let o1 = mdo!(
            p1 <- Product::new_single("A-item".into(), 1000, 2)
            p2 <- Product::new_single("B-item".into(), 2300, 3)
            p3 <- Product::new_single("C-item".into(), 450, 1)
            w1 <- Warehouse::new_logical("stock-1".into())

            c1 <- CartState::new("cart-1".into(), Anonymous.into())
            c2 <- c1.add_item(p1.clone(), w1.clone().into())
            c3 <- c2.add_item(p2.clone(), w1.clone().into())
            c4 <- c3.add_item(p3.clone(), w1.clone().into())

            o1 <- OrderState::new("order-1".into(), c4, Anonymous.into())

            yield o1
        )
        .unwrap();

        let addr = Address::new("123".into(), "abc".into()).unwrap();
        let r = o1.delivery(addr, None);

        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(9350, r.balance());

        if let Ok(x) = Checkout::try_from(r) {
            assert!(x.delivery.is_some());
        } else {
            assert!(false, "invalid type");
        }
    }

    #[test]
    fn delivery_with_cost() {
        let o1 = mdo!(
            p1 <- Product::new_single("A-item".into(), 1000, 2)
            p2 <- Product::new_single("B-item".into(), 2300, 3)
            p3 <- Product::new_single("C-item".into(), 450, 1)
            w1 <- Warehouse::new_logical("stock-1".into())

            c1 <- CartState::new("cart-1".into(), Anonymous.into())
            c2 <- c1.add_item(p1.clone(), w1.clone().into())
            c3 <- c2.add_item(p2.clone(), w1.clone().into())
            c4 <- c3.add_item(p3.clone(), w1.clone().into())

            o1 <- OrderState::new("order-1".into(), c4, Anonymous.into())

            yield o1
        )
        .unwrap();

        let addr = Address::new("123".into(), "abc".into()).unwrap();
        let r = o1.delivery(addr, Some(550));

        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(9900, r.balance());

        if let Ok(x) = Checkout::try_from(r) {
            assert!(x.delivery.is_some());
        } else {
            assert!(false, "invalid type");
        }
    }

    #[test]
    fn delivery_with_negative_cost() {
        let o1 = mdo!(
            p1 <- Product::new_single("A-item".into(), 1000, 2)
            p2 <- Product::new_single("B-item".into(), 2300, 3)
            p3 <- Product::new_single("C-item".into(), 450, 1)
            w1 <- Warehouse::new_logical("stock-1".into())

            c1 <- CartState::new("cart-1".into(), Anonymous.into())
            c2 <- c1.add_item(p1.clone(), w1.clone().into())
            c3 <- c2.add_item(p2.clone(), w1.clone().into())
            c4 <- c3.add_item(p3.clone(), w1.clone().into())

            o1 <- OrderState::new("order-1".into(), c4, Anonymous.into())

            yield o1
        )
        .unwrap();

        let addr = Address::new("123".into(), "abc".into()).unwrap();
        let r = o1.delivery(addr, Some(-550));

        assert!(r.is_err());
    }
}
