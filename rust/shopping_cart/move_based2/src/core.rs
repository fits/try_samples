use chrono::prelude::*;
use macuru::adt;
use rust_decimal::prelude::*;

pub type Amount = Decimal;
pub type Quantity = isize;
pub type ItemId = String;
pub type WarehouseId = String;
pub type CartId = String;
pub type UserId = String;
pub type Date = DateTime<Utc>;

#[derive(Debug, Clone)]
pub struct Move<T, I> {
    target: T,
    from: I,
    to: I,
    #[allow(unused)]
    at: Date,
}

#[derive(Debug, Clone, PartialEq)]
pub struct CartInItem {
    pub item: OrderItem,
    pub from: Location,
}

adt!(
    Movement = ChangeLocation | ChangeOwner | Consecutive derive Debug, Clone with MovementFunc {
        fn items_in_cart(&self, target: &Cart) -> Vec<CartInItem>;
        fn cart_subtotal(&self, target: &Cart) -> Amount;
    }
);

pub type ChangeLocation = Move<OrderItem, Location>;
pub type ChangeOwner = Move<OwnerTarget, Owner>;

#[derive(Debug, Clone)]
pub struct Consecutive(Box<Movement>, Box<Movement>);

adt!(
    OwnerTarget = OrderItem | Cart derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct OrderItem {
    item: Item,
    qty: Quantity,
}

#[derive(Debug, Clone, PartialEq)]
pub struct Item {
    pub id: ItemId,
    pub unit_price: Amount,
}

adt!(
    Owner = System | Anonymous | User derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct System;

#[derive(Debug, Clone, PartialEq)]
pub struct Anonymous;

#[derive(Debug, Clone, PartialEq)]
pub struct User(UserId);

adt!(
    Location = Unknown | Warehouse | Cart derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct Unknown;

#[derive(Debug, Clone, PartialEq)]
pub struct Cart(CartId);

adt!(
    Warehouse = PhysicalWarehouse | LogicalWarehouse derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct PhysicalWarehouse(WarehouseId);

#[derive(Debug, Clone, PartialEq)]
pub struct LogicalWarehouse(WarehouseId);

fn now() -> Date {
    Utc::now()
}

impl Into<Movement> for (Movement, Movement) {
    fn into(self) -> Movement {
        Consecutive(Box::new(self.0), Box::new(self.1)).into()
    }
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

impl<T, I> Move<T, I>
where
    I: PartialEq,
{
    pub fn new(target: T, from: I, to: I) -> Result<Self> {
        if from == to {
            Err("no move, from is to".into())
        } else {
            Ok(Self {
                target,
                from,
                to,
                at: now(),
            })
        }
    }
}

impl OrderItem {
    pub fn new(item_id: ItemId, unit_price: Amount, qty: Quantity) -> Result<Self> {
        let item = Item::new(item_id, unit_price)?;

        Self::try_from((item, qty))
    }

    pub fn item(&self) -> &Item {
        &self.item
    }

    pub fn qty(&self) -> Quantity {
        self.qty
    }

    pub fn qty_invert(&self) -> Self {
        Self {
            item: self.item.clone(),
            qty: self.qty * -1,
        }
    }

    pub fn eq_item(&self, target: &Self) -> bool {
        self.item == target.item
    }

    pub fn subtotal(&self) -> Amount {
        self.item.unit_price * Decimal::from_isize(self.qty).unwrap_or_default()
    }
}

impl TryFrom<(Item, Quantity)> for OrderItem {
    type Error = Box<dyn std::error::Error>;

    fn try_from(value: (Item, Quantity)) -> std::result::Result<Self, Self::Error> {
        let (item, qty) = value;

        if qty < 1 {
            Err(format!("must be qty >= 1, qty={}", qty).into())
        } else {
            Ok(Self { item, qty })
        }
    }
}

impl Item {
    pub fn new(item_id: ItemId, unit_price: Amount) -> Result<Self> {
        if item_id.is_empty() {
            Err("must not be empty item_id".into())
        } else if unit_price < Amount::ZERO {
            Err(format!("must be unit_price >= 0, unit_price={}", unit_price).into())
        } else {
            Ok(Self {
                id: item_id,
                unit_price,
            })
        }
    }
}

impl Cart {
    pub fn new(cart_id: CartId) -> Result<Self> {
        if cart_id.is_empty() {
            Err("must not be empty cart_id".into())
        } else {
            Ok(Self(cart_id))
        }
    }
}

impl Warehouse {
    pub fn new_logical(id: WarehouseId) -> Result<Self> {
        if id.is_empty() {
            Err("must not be empty id".into())
        } else {
            Ok(LogicalWarehouse(id).into())
        }
    }

    pub fn new_physical(id: WarehouseId) -> Result<Self> {
        if id.is_empty() {
            Err("must not be empty id".into())
        } else {
            Ok(PhysicalWarehouse(id).into())
        }
    }
}

impl MovementFunc for ChangeLocation {
    fn items_in_cart(&self, target: &Cart) -> Vec<CartInItem> {
        if is_cart_location(&self.to, target) {
            vec![CartInItem {
                item: self.target.clone(),
                from: self.from.clone(),
            }]
        } else if is_cart_location(&self.from, target) {
            vec![CartInItem {
                item: self.target.qty_invert(),
                from: self.to.clone(),
            }]
        } else {
            Vec::new()
        }
    }

    fn cart_subtotal(&self, target: &Cart) -> Amount {
        if is_cart_location(&self.to, target) {
            self.target.subtotal()
        } else if is_cart_location(&self.from, target) {
            self.target.qty_invert().subtotal()
        } else {
            Amount::zero()
        }
    }
}

impl MovementFunc for ChangeOwner {
    fn items_in_cart(&self, _target: &Cart) -> Vec<CartInItem> {
        Vec::new()
    }

    fn cart_subtotal(&self, _target: &Cart) -> Amount {
        Amount::zero()
    }
}

impl MovementFunc for Consecutive {
    fn items_in_cart(&self, target: &Cart) -> Vec<CartInItem> {
        let a = self.0.items_in_cart(target);
        let b = self.1.items_in_cart(target);

        if a.is_empty() {
            b
        } else {
            let mut a = a;

            for t in b {
                if let Some((i, x)) = a
                    .iter()
                    .enumerate()
                    .find(|(_, x)| x.item.eq_item(&t.item) && x.from == t.from)
                {
                    if x.item.qty + t.item.qty == 0 {
                        a.remove(i);
                    } else {
                        a.get_mut(i).unwrap().item.qty += t.item.qty;
                    }
                } else {
                    a.push(t);
                }
            }

            a.sort_by_key(|x| x.item.item.id.clone());

            a
        }
    }

    fn cart_subtotal(&self, target: &Cart) -> Amount {
        self.0.cart_subtotal(target) + self.1.cart_subtotal(target)
    }
}

fn is_cart_location(loc: &Location, cart: &Cart) -> bool {
    match loc {
        Location::Cart_(x) => x == cart,
        _ => false,
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    fn cons(a: Movement, b: Movement) -> Movement {
        (a, b).into()
    }

    #[test]
    fn new_changeowner() {
        let t = OrderItem::new("A1".into(), dec!(1100), 1).unwrap();

        let r = ChangeOwner::new(t.into(), System.into(), Anonymous.into());

        assert!(r.is_ok());
    }

    #[test]
    fn new_changeowner_not_move() {
        let t = OrderItem::new("A1".into(), dec!(1100), 1).unwrap();

        let r = ChangeOwner::new(t.into(), System.into(), System.into());

        assert!(r.is_err());
    }

    #[test]
    fn new_item() {
        let r = Item::new("A1".into(), dec!(100));

        assert!(r.is_ok());
    }

    #[test]
    fn new_item_with_negative_price() {
        let r = Item::new("A1".into(), dec!(-100));

        assert!(r.is_err());
    }

    #[test]
    fn new_item_with_empty_id() {
        let r = Item::new("".into(), dec!(100));

        assert!(r.is_err());
    }

    #[test]
    fn new_order_item() {
        let r = OrderItem::new("A1".into(), dec!(100), 1);

        assert!(r.is_ok());
    }

    #[test]
    fn new_order_item_with_qty_zero() {
        let r = OrderItem::new("A1".into(), dec!(-100), 0);

        assert!(r.is_err());
    }

    #[test]
    fn items_in_cart_changeowner() {
        let cart = Cart("cart-1".into());

        let m = ChangeOwner::new(cart.clone().into(), System.into(), Anonymous.into()).unwrap();

        let r = m.items_in_cart(&cart);

        assert!(r.is_empty());
    }

    #[test]
    fn items_in_cart_changelocation() {
        let cart = Cart("cart-1".into());

        let t = OrderItem::new("A1".into(), dec!(1100), 2).unwrap();

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = ChangeLocation::new(t.clone().into(), w.clone(), cart.clone().into()).unwrap();

        let r = m.items_in_cart(&cart);

        assert_eq!(1, r.len());

        let it = r.first().unwrap();

        assert_eq!(t, it.item);
        assert_eq!(w, it.from);
    }

    #[test]
    fn items_in_cart_changelocation_other_cart() {
        let cart = Cart("cart-1".into());

        let t = OrderItem::new("A1".into(), dec!(1100), 2).unwrap();

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = ChangeLocation::new(t.clone().into(), w.clone(), cart.into()).unwrap();

        let r = m.items_in_cart(&Cart("cart-2".into()));

        assert!(r.is_empty());
    }

    #[test]
    fn items_in_cart_changelocation_out() {
        let cart = Cart("cart-1".into());

        let t = OrderItem::new("A1".into(), dec!(1100), 3).unwrap();

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = ChangeLocation::new(t.clone().into(), cart.clone().into(), w.clone()).unwrap();

        let r = m.items_in_cart(&cart);

        assert_eq!(1, r.len());

        let it = r.first().unwrap();

        assert_eq!(t.qty_invert(), it.item);
        assert_eq!(w, it.from);
    }

    #[test]
    fn items_in_cart_cons_simple() {
        let cart = Cart("cart-1".into());

        let t = OrderItem::new("A1".into(), dec!(1100), 2).unwrap();

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = cons(
            ChangeOwner::new(cart.clone().into(), System.into(), Anonymous.into())
                .unwrap()
                .into(),
            ChangeLocation::new(t.clone().into(), w.clone(), cart.clone().into())
                .unwrap()
                .into(),
        );

        let r = m.items_in_cart(&cart);

        assert_eq!(1, r.len());

        let i1 = r.first().unwrap();

        assert_eq!(t, i1.item);
        assert_eq!(w, i1.from);
    }

    #[test]
    fn items_in_cart_cons() {
        let cart = Cart("cart-1".into());

        let item1 = Item {
            id: "A1".into(),
            unit_price: dec!(1100),
        };

        let item2 = Item {
            id: "B2".into(),
            unit_price: dec!(220),
        };

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = cons(
            cons(
                ChangeLocation::new(
                    (item1.clone(), 2).try_into().unwrap(),
                    w.clone(),
                    cart.clone().into(),
                )
                .unwrap()
                .into(),
                ChangeLocation::new(
                    (item2.clone(), 1).try_into().unwrap(),
                    w.clone(),
                    cart.clone().into(),
                )
                .unwrap()
                .into(),
            ),
            ChangeLocation::new(
                (item1.clone(), 3).try_into().unwrap(),
                w.clone(),
                cart.clone().into(),
            )
            .unwrap()
            .into(),
        );

        let r = m.items_in_cart(&cart);

        assert_eq!(2, r.len());

        let i1 = r.first().unwrap();

        assert_eq!(item1, i1.item.item);
        assert_eq!(5, i1.item.qty);
        assert_eq!(w, i1.from);

        let i2 = r.last().unwrap();

        assert_eq!(item2, i2.item.item);
        assert_eq!(1, i2.item.qty);
        assert_eq!(w, i2.from);
    }

    #[test]
    fn items_in_cart_cons_other_location() {
        let cart = Cart("cart-1".into());

        let item1 = Item {
            id: "A1".into(),
            unit_price: dec!(1100),
        };

        let item2 = Item {
            id: "B2".into(),
            unit_price: dec!(220),
        };

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();
        let w2: Location = Warehouse::new_logical("stock-2".into()).unwrap().into();

        let m = cons(
            cons(
                ChangeLocation::new(
                    (item1.clone(), 2).try_into().unwrap(),
                    w.clone(),
                    cart.clone().into(),
                )
                .unwrap()
                .into(),
                ChangeLocation::new(
                    (item2.clone(), 1).try_into().unwrap(),
                    w.clone(),
                    cart.clone().into(),
                )
                .unwrap()
                .into(),
            ),
            ChangeLocation::new(
                (item1.clone(), 3).try_into().unwrap(),
                w2.clone(),
                cart.clone().into(),
            )
            .unwrap()
            .into(),
        );

        let r = m.items_in_cart(&cart);

        assert_eq!(3, r.len());

        let i1 = r.first().unwrap();

        assert_eq!(item1, i1.item.item);
        assert_eq!(2, i1.item.qty);
        assert_eq!(w, i1.from);

        let i2 = r.get(1).unwrap();

        assert_eq!(item1, i2.item.item);
        assert_eq!(3, i2.item.qty);
        assert_eq!(w2, i2.from);

        let i3 = r.last().unwrap();

        assert_eq!(item2, i3.item.item);
        assert_eq!(1, i3.item.qty);
        assert_eq!(w, i3.from);
    }

    #[test]
    fn items_in_cart_cons_inout() {
        let cart = Cart("cart-1".into());

        let item1 = Item {
            id: "A1".into(),
            unit_price: dec!(1100),
        };

        let item2 = Item {
            id: "B2".into(),
            unit_price: dec!(220),
        };

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = cons(
            cons(
                cons(
                    ChangeLocation::new(
                        (item1.clone(), 2).try_into().unwrap(),
                        w.clone(),
                        cart.clone().into(),
                    )
                    .unwrap()
                    .into(),
                    ChangeLocation::new(
                        (item2.clone(), 1).try_into().unwrap(),
                        w.clone(),
                        cart.clone().into(),
                    )
                    .unwrap()
                    .into(),
                ),
                cons(
                    ChangeLocation::new(
                        (item1.clone(), 3).try_into().unwrap(),
                        w.clone(),
                        cart.clone().into(),
                    )
                    .unwrap()
                    .into(),
                    ChangeLocation::new(
                        (item2.clone(), 1).try_into().unwrap(),
                        cart.clone().into(),
                        w.clone(),
                    )
                    .unwrap()
                    .into(),
                ),
            ),
            ChangeLocation::new(
                (item1.clone(), 1).try_into().unwrap(),
                cart.clone().into(),
                w.clone(),
            )
            .unwrap()
            .into(),
        );

        let r = m.items_in_cart(&cart);

        assert_eq!(1, r.len());

        let i1 = r.first().unwrap();

        assert_eq!(item1, i1.item.item);
        assert_eq!(4, i1.item.qty);
        assert_eq!(w, i1.from);
    }

    #[test]
    fn cart_subtotal_changeowner() {
        let cart = Cart("cart-1".into());

        let m = ChangeOwner::new(cart.clone().into(), System.into(), Anonymous.into()).unwrap();

        let r = m.cart_subtotal(&cart);

        assert_eq!(Amount::zero(), r);
    }

    #[test]
    fn cart_subtotal_changelocation() {
        let cart = Cart("cart-1".into());

        let t = OrderItem::new("A1".into(), dec!(1100), 2).unwrap();

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = ChangeLocation::new(t.clone().into(), w.clone(), cart.clone().into()).unwrap();

        let r = m.cart_subtotal(&cart);

        assert_eq!(dec!(2200), r);
    }

    #[test]
    fn cart_subtotal_changelocation_other_cart() {
        let cart = Cart("cart-1".into());

        let t = OrderItem::new("A1".into(), dec!(1100), 2).unwrap();

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = ChangeLocation::new(t.clone().into(), w.clone(), cart.into()).unwrap();

        let r = m.cart_subtotal(&Cart("cart-2".into()));

        assert_eq!(Amount::zero(), r);
    }

    #[test]
    fn cart_subtotal_changelocation_out() {
        let cart = Cart("cart-1".into());

        let t = OrderItem::new("A1".into(), dec!(1100), 3).unwrap();

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = ChangeLocation::new(t.clone().into(), cart.clone().into(), w.clone()).unwrap();

        let r = m.cart_subtotal(&cart);

        assert_eq!(dec!(-3300), r);
    }

    #[test]
    fn cart_subtotal_cons_simple() {
        let cart = Cart("cart-1".into());

        let t = OrderItem::new("A1".into(), dec!(1100), 2).unwrap();

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = cons(
            ChangeOwner::new(cart.clone().into(), System.into(), Anonymous.into())
                .unwrap()
                .into(),
            ChangeLocation::new(t.clone().into(), w.clone(), cart.clone().into())
                .unwrap()
                .into(),
        );

        let r = m.cart_subtotal(&cart);

        assert_eq!(dec!(2200), r);
    }

    #[test]
    fn cart_subtotal_cons_inout() {
        let cart = Cart("cart-1".into());

        let item1 = Item {
            id: "A1".into(),
            unit_price: dec!(1100),
        };

        let item2 = Item {
            id: "B2".into(),
            unit_price: dec!(220),
        };

        let w: Location = Warehouse::new_logical("stock-1".into()).unwrap().into();

        let m = cons(
            cons(
                cons(
                    ChangeLocation::new(
                        (item1.clone(), 2).try_into().unwrap(),
                        w.clone(),
                        cart.clone().into(),
                    )
                    .unwrap()
                    .into(),
                    ChangeLocation::new(
                        (item2.clone(), 1).try_into().unwrap(),
                        w.clone(),
                        cart.clone().into(),
                    )
                    .unwrap()
                    .into(),
                ),
                cons(
                    ChangeLocation::new(
                        (item1.clone(), 3).try_into().unwrap(),
                        w.clone(),
                        cart.clone().into(),
                    )
                    .unwrap()
                    .into(),
                    ChangeLocation::new(
                        (item2.clone(), 1).try_into().unwrap(),
                        cart.clone().into(),
                        w.clone(),
                    )
                    .unwrap()
                    .into(),
                ),
            ),
            ChangeLocation::new(
                (item1.clone(), 1).try_into().unwrap(),
                cart.clone().into(),
                w.clone(),
            )
            .unwrap()
            .into(),
        );

        let r = m.cart_subtotal(&cart);

        assert_eq!(dec!(4400), r);
    }
}
