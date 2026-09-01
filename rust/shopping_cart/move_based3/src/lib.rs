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
    OrderState = Checkout | Paying | Confirmed derive Debug, Clone
);

#[derive(Debug, Clone)]
pub struct Checkout {
    order: Order,
    history: Movement,
}

#[derive(Debug, Clone)]
pub struct Paying;

#[derive(Debug, Clone)]
pub struct Confirmed;

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
            }
            .into())
        } else {
            Err("can not create order".into())
        }
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

        assert!(r.is_ok())
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
}
