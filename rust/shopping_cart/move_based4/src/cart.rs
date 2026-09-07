use super::core::*;
use macuru::adt;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

adt!(
    CartState = EmptyCart | ActiveCart derive Debug, Clone with CartStateFunc {
        fn add_item(&self, product: Product, from: Warehouse) -> Result<Self>;
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
    items: Vec<CartItem>,
    subtotal: Amount,
    history: Movement,
}

#[derive(Debug, Clone)]
pub struct CartItem {
    product: Product,
    from: Location,
}

impl CartState {
    pub fn new(cart_id: CartId, owner: Owner) -> Result<Self> {
        let cart = Cart::new(cart_id)?;

        let target = MoveResource::new(cart.clone().into(), System.into());

        let history = Movement::new(target, owner.into(), None)?;

        Ok(EmptyCart { cart, history }.into())
    }
}

impl CartStateFunc for EmptyCart {
    fn add_item(&self, product: Product, from: Warehouse) -> Result<CartState> {
        let target = MoveResource::new(product.clone().into(), from.clone().into());

        let mv = Movement::new(target, self.cart.clone().into(), None)?;
        let history = self.history.clone().cons(mv);

        let subtotal = product.subtotal();
        let items = items_in_cart(&history, &self.cart)?;

        Ok(ActiveCart {
            cart: self.cart.clone(),
            items,
            subtotal,
            history,
        }
        .into())
    }
}

impl CartStateFunc for ActiveCart {
    fn add_item(&self, product: Product, from: Warehouse) -> Result<CartState> {
        let target = MoveResource::new(product.clone().into(), from.clone().into());

        let mv = Movement::new(target, self.cart.clone().into(), None)?;
        let history = self.history.clone().cons(mv);

        let subtotal = self.subtotal + product.subtotal();
        let items = items_in_cart(&history, &self.cart)?;

        Ok(Self {
            cart: self.cart.clone(),
            items,
            subtotal,
            history,
        }
        .into())
    }
}

fn items_in_cart(mv: &Movement, cart: &Cart) -> Result<Vec<CartItem>> {
    let mut res = Vec::new();

    for x in mv.last_targets()? {
        if let Ok(p) = Product::try_from(x.resource().clone())
            && x.current().eq_cart(cart)
        {
            let from = x.prev().clone().unwrap_or(Unknown.into());

            res.push(CartItem { product: p, from });
        }
    }

    Ok(res)
}

#[cfg(test)]
mod tests {
    use super::*;
    use macuru::{MonadLike, mdo};
    use rust_decimal::prelude::*;

    fn single_item(id: &str, unit_price: Amount, qty: Quantity) -> Product {
        SingleItem::new(id.into(), unit_price, qty).unwrap().into()
    }

    fn cart(id: &str) -> Cart {
        Cart::new(id.into()).unwrap()
    }

    #[test]
    fn cart_new() {
        if let Ok(CartState::EmptyCart_(x)) = CartState::new("cart-1".into(), Anonymous.into()) {
            assert_eq!(cart("cart-1"), x.cart);
        } else {
            assert!(false, "failed new")
        }
    }

    #[test]
    fn cart_new_with_system() {
        let r = CartState::new("cart-1".into(), System.into());
        assert!(r.is_err());
    }

    #[test]
    fn empty_cart_add_item() {
        let s = CartState::new("cart-1".into(), Anonymous.into()).unwrap();
        let w = Warehouse::new_logical("stock-1".into()).unwrap();

        if let Ok(CartState::ActiveCart_(x)) =
            s.add_item(single_item("A1", dec!(1200), 3), w.clone().into())
        {
            assert_eq!(cart("cart-1"), x.cart);
            assert_eq!(dec!(3600), x.subtotal);

            assert_eq!(1, x.items.len());

            let t = x.items.first().unwrap();

            assert_eq!(single_item("A1", dec!(1200), 3), t.product);
            assert_eq!(Location::from(w), t.from);
        } else {
            assert!(false, "failed add_item")
        }
    }

    #[test]
    fn active_cart_add_item() {
        let w = Warehouse::new_logical("stock-1".into()).unwrap();

        let s = mdo!(
            a <- CartState::new("cart-1".into(), Anonymous.into())
            b <- a.add_item(single_item("A1", dec!(340), 2), w.clone().into())
            yield b
        )
        .unwrap();

        if let Ok(CartState::ActiveCart_(x)) =
            s.add_item(single_item("B2", dec!(1000), 1), w.clone().into())
        {
            assert_eq!(cart("cart-1"), x.cart);
            assert_eq!(dec!(1680), x.subtotal);

            assert_eq!(2, x.items.len());

            let t = x.items.last().unwrap();

            assert_eq!(single_item("B2", dec!(1000), 1), t.product);
            assert_eq!(Location::from(w), t.from);
        } else {
            assert!(false, "failed add_item")
        }
    }
}
