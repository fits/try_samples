use std::fmt::Display;

use serde::{Deserialize, Serialize};

pub type CartId = String;
pub type ItemCode = String;
pub type Amount = i64;
pub type Quantity = i64;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "type")]
pub enum Cart {
    Empty(EmptyCart),
    Active(ActiveCart),
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct EmptyCart {
    cart_id: CartId,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ActiveCart {
    cart_id: CartId,
    items: Vec<CartItem>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct CartItem {
    item: Item,
    qty: Quantity,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Item {
    code: ItemCode,
    unit_price: Amount,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct StockedItem {
    item: Item,
    qty: Quantity,
}

impl Cart {
    pub fn new(cart_id: CartId) -> Result<Self> {
        check_empty(&cart_id, "cart_id")?;

        Ok(Self::Empty(EmptyCart { cart_id }))
    }

    pub fn cart_id(&self) -> &CartId {
        match self {
            Self::Empty(x) => &x.cart_id,
            Self::Active(x) => &x.cart_id,
        }
    }

    pub fn add_item(&self, item: Item, qty: Quantity) -> Result<Self> {
        let cart_item = CartItem::new(item, qty)?;

        let res = match self {
            Self::Empty(EmptyCart { cart_id }) => Self::Active(ActiveCart {
                cart_id: cart_id.clone(),
                items: vec![cart_item],
            }),
            Self::Active(ActiveCart { cart_id, items }) => {
                let mut new_items = items.clone();
                new_items.push(cart_item);

                Self::Active(ActiveCart {
                    cart_id: cart_id.clone(),
                    items: new_items,
                })
            }
        };

        Ok(res)
    }
}

impl CartItem {
    pub fn new(item: Item, qty: Quantity) -> Result<Self> {
        check_less(&qty, &1, "qty")?;

        Ok(Self { item, qty })
    }
}

impl Item {
    pub fn new(code: ItemCode, unit_price: Amount) -> Result<Self> {
        check_empty(&code, "code")?;
        check_less_default(&unit_price, "unit_price")?;

        Ok(Self { code, unit_price })
    }
}

impl StockedItem {
    pub fn new(item: Item) -> Self {
        Self { item, qty: 0 }
    }

    pub fn item(&self) -> &Item {
        &self.item
    }

    pub fn item_code(&self) -> &ItemCode {
        &self.item.code
    }

    pub fn store_in(&self, qty: Quantity) -> Result<Self> {
        check_less(&qty, &1, "qty")?;

        Ok(Self {
            item: self.item.clone(),
            qty: self.qty + qty,
        })
    }

    pub fn store_out(&self, qty: Quantity) -> Result<Self> {
        check_less(&qty, &1, "qty")?;

        let new_qty = self.qty - qty;

        check_less_default(&new_qty, "stocked qty")?;

        Ok(Self {
            item: self.item.clone(),
            qty: new_qty,
        })
    }
}

fn check_empty(value: &String, prop: &str) -> Result<()> {
    if value.trim().is_empty() {
        Err(format!("{} must not be empty", prop).into())
    } else {
        Ok(())
    }
}

fn check_less<T>(value: &T, target: &T, prop: &str) -> Result<()>
where
    T: Ord + Display,
{
    if value < target {
        Err(format!("must not be {} < {}", prop, target).into())
    } else {
        Ok(())
    }
}

fn check_less_default<T>(value: &T, prop: &str) -> Result<()>
where
    T: Ord + Default + Display,
{
    check_less(value, &T::default(), prop)
}
