use chrono::prelude::*;
use macuru::adt;
use rust_decimal::prelude::*;

pub type Amount = Decimal;
pub type Quantity = i32;
pub type Date = DateTime<Utc>;
pub type CartId = String;
pub type ItemId = String;
pub type WarehouseId = String;
pub type UserId = String;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

adt!(
    Movement = SingleMove | ConsecutiveMove derive Debug, Clone with MovementFunc {
        fn last_targets(&self) -> Result<Vec<MoveResource>>;
    }
);

#[derive(Debug, Clone)]
pub struct SingleMove {
    target: MoveResource,
    to: Location,
    cost: Option<Amount>,
    at: Date,
}

#[derive(Debug, Clone)]
pub struct ConsecutiveMove(Box<Movement>, Box<Movement>);

impl Movement {
    pub fn new(target: MoveResource, to: Location, cost: Option<Amount>) -> Result<Self> {
        target.validate_move_to(&to)?;

        Ok(SingleMove {
            target,
            to,
            cost,
            at: now(),
        }
        .into())
    }

    pub fn cons(self, m: Movement) -> Self {
        ConsecutiveMove(Box::new(self), Box::new(m)).into()
    }
}

impl MovementFunc for SingleMove {
    fn last_targets(&self) -> Result<Vec<MoveResource>> {
        let next_target = self.target.move_to(self.to.clone())?;
        Ok(vec![next_target])
    }
}

impl MovementFunc for ConsecutiveMove {
    fn last_targets(&self) -> Result<Vec<MoveResource>> {
        let a = self.0.last_targets()?;
        let b = self.1.last_targets()?;

        if a.is_empty() {
            Ok(b)
        } else {
            let mut a = a;

            for r in b {
                let x = a.iter().enumerate().find(|(_, x)| r.is_next(x));

                if let Some((i, _)) = x {
                    a.remove(i);
                }

                a.push(r);
            }

            Ok(a)
        }
    }
}

#[derive(Debug, Clone)]
pub struct MoveResource {
    resource: Resource,
    current: Location,
    prev: Option<Location>,
}

impl MoveResource {
    pub fn new(resource: Resource, location: Location) -> Self {
        MoveResource {
            resource,
            current: location,
            prev: None,
        }
    }

    pub fn resource(&self) -> &Resource {
        &self.resource
    }

    pub fn current(&self) -> &Location {
        &self.current
    }

    pub fn prev(&self) -> &Option<Location> {
        &self.prev
    }

    fn move_to(&self, to: Location) -> Result<Self> {
        self.validate_move_to(&to)?;

        Ok(Self {
            resource: self.resource.clone(),
            current: to,
            prev: Some(self.current.clone()),
        })
    }

    fn validate_move_to(&self, to: &Location) -> Result<()> {
        if self.current == *to {
            Err("no move".into())
        } else {
            Ok(())
        }
    }

    fn is_next(&self, prev_state: &Self) -> bool {
        if let Some(x) = &self.prev {
            self.resource == prev_state.resource && x == &prev_state.current
        } else {
            false
        }
    }
}

adt!(
    Resource = Product | Cart derive Debug, Clone, PartialEq
);

adt!(
    Product = SingleItem | BundleItems derive Debug, Clone, PartialEq with ProductFunc {
        fn subtotal(&self) -> Amount;
        fn id(&self) -> &ItemId;
        fn unit_price(&self) -> Amount;
        fn qty(&self) -> Quantity;
    }
);

#[derive(Debug, Clone, PartialEq)]
pub struct SingleItem {
    item_id: ItemId,
    unit_price: Amount,
    qty: Quantity,
}

#[derive(Debug, Clone, PartialEq)]
pub struct BundleItems {
    bundle_id: ItemId,
    items: Vec<SingleItem>,
    unit_price: Amount,
    qty: Quantity,
}

impl SingleItem {
    pub fn new(item_id: ItemId, unit_price: Amount, qty: Quantity) -> Result<Self> {
        validate_empty(&item_id, Some("item_id"))?;
        validate_negative(&unit_price, Some("unit_price"))?;
        validate_lessthan(1, &qty, Some("qty"))?;

        Ok(SingleItem {
            item_id,
            unit_price,
            qty,
        })
    }
}

impl BundleItems {
    pub fn new(
        bundle_id: ItemId,
        items: Vec<SingleItem>,
        unit_price: Amount,
        qty: Quantity,
    ) -> Result<Self> {
        validate_empty(&bundle_id, Some("bundle_id"))?;
        validate_negative(&unit_price, Some("unit_price"))?;
        validate_lessthan(1, &qty, Some("qty"))?;

        let mut items_qty = 0;
        let mut items_subtotal = Amount::ZERO;

        for t in &items {
            items_qty += t.qty;
            items_subtotal += t.subtotal();
        }

        validate_lessthan(2, &items_qty, Some("items_qty"))?;

        let discount = items_subtotal - unit_price;
        validate_negative(&discount, Some("discount(items_subtotal - unit_price)"))?;

        Ok(BundleItems {
            bundle_id,
            items,
            unit_price,
            qty,
        })
    }
}

impl ProductFunc for SingleItem {
    fn subtotal(&self) -> Amount {
        self.unit_price * Decimal::from_i32(self.qty).unwrap_or_default()
    }

    fn id(&self) -> &ItemId {
        &self.item_id
    }

    fn unit_price(&self) -> Amount {
        self.unit_price
    }

    fn qty(&self) -> Quantity {
        self.qty
    }
}

impl ProductFunc for BundleItems {
    fn subtotal(&self) -> Amount {
        self.unit_price * Decimal::from_i32(self.qty).unwrap_or_default()
    }

    fn id(&self) -> &ItemId {
        &self.bundle_id
    }

    fn unit_price(&self) -> Amount {
        self.unit_price
    }

    fn qty(&self) -> Quantity {
        self.qty
    }
}

impl From<SingleItem> for Resource {
    fn from(value: SingleItem) -> Self {
        Product::from(value).into()
    }
}

impl From<BundleItems> for Resource {
    fn from(value: BundleItems) -> Self {
        Product::from(value).into()
    }
}

adt!(
    Location = Unknown | Warehouse | Cart | Owner derive Debug, Clone, PartialEq
);

impl Location {
    pub fn eq_cart(&self, cart: &Cart) -> bool {
        match self {
            Self::Cart_(x) => x == cart,
            _ => false,
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct Unknown;

adt!(
    Warehouse = LogicalWarehouse | PhysicalWarehouse derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct LogicalWarehouse(WarehouseId);

#[derive(Debug, Clone, PartialEq)]
pub struct PhysicalWarehouse(WarehouseId);

impl Warehouse {
    pub fn new_logical(warehouse_id: WarehouseId) -> Result<Self> {
        validate_empty(&warehouse_id, Some("warehouse_id"))?;

        Ok(LogicalWarehouse(warehouse_id).into())
    }

    pub fn new_physical(warehouse_id: WarehouseId) -> Result<Self> {
        validate_empty(&warehouse_id, Some("warehouse_id"))?;

        Ok(PhysicalWarehouse(warehouse_id).into())
    }
}

impl From<LogicalWarehouse> for Location {
    fn from(value: LogicalWarehouse) -> Self {
        Warehouse::from(value).into()
    }
}

impl From<PhysicalWarehouse> for Location {
    fn from(value: PhysicalWarehouse) -> Self {
        Warehouse::from(value).into()
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct Cart(CartId);

impl Cart {
    pub fn new(cart_id: CartId) -> Result<Self> {
        validate_empty(&cart_id, Some("cart_id"))?;

        Ok(Self(cart_id.into()))
    }
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

impl User {
    pub fn new(user_id: UserId) -> Result<Self> {
        validate_empty(&user_id, Some("user_id"))?;

        Ok(Self(user_id.into()))
    }
}

impl From<System> for Location {
    fn from(value: System) -> Self {
        Owner::from(value).into()
    }
}

impl From<Anonymous> for Location {
    fn from(value: Anonymous) -> Self {
        Owner::from(value).into()
    }
}

impl From<User> for Location {
    fn from(value: User) -> Self {
        Owner::from(value).into()
    }
}

fn now() -> Date {
    Utc::now()
}

fn validate_empty(value: &str, prop: Option<&str>) -> Result<()> {
    if value.is_empty() {
        Err(format!("{} must not be empty", prop.unwrap_or_default()).into())
    } else {
        Ok(())
    }
}

fn validate_negative(value: &Amount, prop: Option<&str>) -> Result<()> {
    if value.is_sign_negative() {
        Err(format!(
            "must not be nagative value, {}={}",
            prop.unwrap_or_default(),
            value
        )
        .into())
    } else {
        Ok(())
    }
}

fn validate_lessthan(target: Quantity, value: &Quantity, prop: Option<&str>) -> Result<()> {
    if *value < target {
        let prop = prop.unwrap_or_default();
        Err(format!("must not be {} < {}, {}={}", prop, target, prop, value).into())
    } else {
        Ok(())
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use macuru::{MonadLike, mdo};

    #[test]
    fn cart_new_empty_id() {
        let r = Cart::new("".into());
        assert!(r.is_err());
    }

    #[test]
    fn singleitem_new() {
        let r1 = SingleItem::new("A1".into(), dec!(100), 1);
        assert!(r1.is_ok());
    }

    #[test]
    fn singleitem_new_invalid_param() {
        let r1 = SingleItem::new("".into(), dec!(100), 2);
        assert!(r1.is_err());

        let r2 = SingleItem::new("A1".into(), dec!(-100), 2);
        assert!(r2.is_err());

        let r3 = SingleItem::new("A1".into(), dec!(100), 0);
        assert!(r3.is_err());
    }

    #[test]
    fn bundleitems_new() {
        let items = vec![
            SingleItem::new("A1".into(), dec!(120), 3).unwrap(),
            SingleItem::new("B2".into(), dec!(2500), 1).unwrap(),
        ];

        let r = BundleItems::new("set-1".into(), items, dec!(2500), 2);

        assert!(r.is_ok());
    }

    #[test]
    fn bundleitems_new_single_item_two() {
        let items = vec![SingleItem::new("A1".into(), dec!(120), 2).unwrap()];

        let r = BundleItems::new("set-1".into(), items, dec!(200), 1);

        assert!(r.is_ok());
    }

    #[test]
    fn bundleitems_new_single_item() {
        let items = vec![SingleItem::new("A1".into(), dec!(120), 1).unwrap()];

        let r = BundleItems::new("set-1".into(), items, dec!(100), 1);

        assert!(r.is_err());
    }

    #[test]
    fn bundleitems_empty_item() {
        let items = vec![];

        let r = BundleItems::new("set-1".into(), items, dec!(100), 1);

        assert!(r.is_err());
    }

    #[test]
    fn bundleitems_new_same_price() {
        let items = vec![
            SingleItem::new("A1".into(), dec!(120), 3).unwrap(),
            SingleItem::new("B2".into(), dec!(2500), 1).unwrap(),
        ];

        let r = BundleItems::new("set-1".into(), items, dec!(2860), 1);

        assert!(r.is_ok());
    }

    #[test]
    fn bundleitems_new_over_price() {
        let items = vec![
            SingleItem::new("A1".into(), dec!(120), 3).unwrap(),
            SingleItem::new("B2".into(), dec!(2500), 1).unwrap(),
        ];

        let r = BundleItems::new("set-1".into(), items, dec!(2900), 1);

        assert!(r.is_err());
    }

    #[test]
    fn singleitem_subtotal() {
        let r = SingleItem::new("A1".into(), dec!(120), 3).unwrap();

        assert_eq!(dec!(360), r.subtotal());
    }

    #[test]
    fn bundleitems_subtotal() {
        let items = vec![
            SingleItem::new("A1".into(), dec!(120), 3).unwrap(),
            SingleItem::new("B2".into(), dec!(2500), 1).unwrap(),
        ];

        let r = BundleItems::new("set-1".into(), items, dec!(2500), 2).unwrap();

        assert_eq!(dec!(5000), r.subtotal());
    }

    #[test]
    fn movement_new() {
        let t = MoveResource::new(Cart::new("c1".into()).unwrap().into(), System.into());

        let r = Movement::new(t, Anonymous.into(), None);

        assert!(r.is_ok());

        let r = r.unwrap();

        let s = SingleMove::try_from(r);

        assert!(s.is_ok());
    }

    #[test]
    fn movement_new_nomove() {
        let t = MoveResource::new(Cart::new("c1".into()).unwrap().into(), System.into());

        let r = Movement::new(t, System.into(), None);

        assert!(r.is_err());
    }

    #[test]
    fn singlemove_last_targets() {
        let t = MoveResource::new(Cart::new("c1".into()).unwrap().into(), System.into());

        let m = Movement::new(t, Anonymous.into(), None).unwrap();

        let r = m.last_targets();

        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(1, r.len());

        if let Some(x) = r.first() {
            assert_eq!(Resource::from(Cart::new("c1".into()).unwrap()), x.resource);
            assert_eq!(Location::from(Anonymous), x.current);
            assert_eq!(Some(Location::from(System)), x.prev);
        } else {
            assert!(false, "failed last_targets");
        }
    }

    #[test]
    fn consmove_last_targets() {
        let s = mdo!(
            c <- Cart::new("c1".into())
            w1 <- Warehouse::new_logical("w1".into())
            w2 <- Warehouse::new_logical("w2".into())
            i1 <- SingleItem::new("A1".into(), dec!(120), 3)
            i2 <- SingleItem::new("B2".into(), dec!(340), 1)

            m1 <- Movement::new(MoveResource::new(c.clone().into(), System.into()), Anonymous.into(), None)
            m2 <- Movement::new(MoveResource::new(i1.clone().into(), w1.clone().into()), c.clone().into(), None)
            m3 <- Movement::new(MoveResource::new(i2.clone().into(), w1.clone().into()), c.clone().into(), None)
            m4 <- Movement::new(MoveResource::new(i2.clone().into(), c.clone().into()), w2.clone().into(), None)

            yield m1.clone().cons(m2.clone()).cons(m3.clone()).cons(m4.clone())
        ).unwrap();

        let r = s.last_targets();
        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(3, r.len());

        if let Some(x) = r.first() {
            assert_eq!(Resource::from(Cart::new("c1".into()).unwrap()), x.resource);
            assert_eq!(Location::from(Anonymous), x.current);
            assert_eq!(Some(Location::from(System)), x.prev);
        } else {
            assert!(false, "failed first");
        }

        if let Some(x) = r.last() {
            assert_eq!(
                Resource::from(SingleItem::new("B2".into(), dec!(340), 1).unwrap()),
                x.resource
            );
            assert_eq!(
                Location::from(Warehouse::new_logical("w2".into()).unwrap()),
                x.current
            );
            assert_eq!(
                Some(Location::from(Cart::new("c1".into()).unwrap())),
                x.prev
            );
        } else {
            assert!(false, "failed last");
        }
    }
}
