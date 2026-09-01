use macuru::adt;

pub type Amount = i64;
pub type Quantity = i64;
pub type UserId = String;
pub type CartId = String;
pub type WarehouseId = String;
pub type ItemId = String;
pub type BundleId = String;
pub type OrderId = String;
pub type PaymentId = String;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

adt!(
    MoveResource = Owned | Placed derive Debug, Clone with MoveResourceFunc {
        fn resource(&self) -> &Resource;
        fn owner(&self) -> &Owner;
        fn location(&self) -> Option<&Location>;
        fn next_owner(&self, to: Owner) -> Result<Self>;
        fn next_location(&self, to: Location) -> Result<Self>;
        fn is_next(&self, old: &MoveResource) -> Result<bool>;
    }
);

#[derive(Debug, Clone)]
pub struct Owned {
    resource: Resource,
    owner: Owner,
    old_owner: Option<Owner>,
}

#[derive(Debug, Clone)]
pub struct Placed {
    resource: Resource,
    owner: Owner,
    location: Location,
    old_owner: Option<Owner>,
    old_location: Option<Location>,
}

impl MoveResource {
    pub fn new(resource: Resource, owner: Owner, location: Option<Location>) -> Self {
        if let Some(location) = location {
            Placed {
                resource,
                owner,
                location,
                old_owner: None,
                old_location: None,
            }
            .into()
        } else {
            Owned {
                resource,
                owner,
                old_owner: None,
            }
            .into()
        }
    }
}

impl MoveResourceFunc for Owned {
    fn resource(&self) -> &Resource {
        &self.resource
    }

    fn owner(&self) -> &Owner {
        &self.owner
    }

    fn location(&self) -> Option<&Location> {
        None
    }

    fn next_owner(&self, to: Owner) -> Result<MoveResource> {
        if self.owner == to {
            error_no_move()
        } else {
            Ok(Self {
                resource: self.resource.clone(),
                owner: to,
                old_owner: Some(self.owner.clone()),
            }
            .into())
        }
    }

    fn next_location(&self, _to: Location) -> Result<MoveResource> {
        error_not_supported()
    }

    fn is_next(&self, old: &MoveResource) -> Result<bool> {
        match old {
            MoveResource::Owned_(x) => {
                if self.resource == x.resource
                    && let Some(o) = &self.old_owner
                {
                    Ok(o == &x.owner)
                } else {
                    Ok(false)
                }
            }
            _ => Err("mismatch type".into()),
        }
    }
}

impl MoveResourceFunc for Placed {
    fn resource(&self) -> &Resource {
        &self.resource
    }

    fn owner(&self) -> &Owner {
        &self.owner
    }

    fn location(&self) -> Option<&Location> {
        Some(&self.location)
    }

    fn next_owner(&self, to: Owner) -> Result<MoveResource> {
        if self.owner == to {
            error_no_move()
        } else {
            Ok(Self {
                resource: self.resource.clone(),
                owner: to,
                location: self.location.clone(),
                old_owner: Some(self.owner.clone()),
                old_location: self.old_location.clone(),
            }
            .into())
        }
    }

    fn next_location(&self, to: Location) -> Result<MoveResource> {
        if self.location == to {
            error_no_move()
        } else {
            Ok(Self {
                resource: self.resource.clone(),
                owner: self.owner.clone(),
                location: to,
                old_owner: self.old_owner.clone(),
                old_location: Some(self.location.clone()),
            }
            .into())
        }
    }

    fn is_next(&self, old: &MoveResource) -> Result<bool> {
        match old {
            MoveResource::Placed_(x) => {
                if self.resource == x.resource {
                    if let Some(o) = &self.old_owner
                        && o == &x.owner
                    {
                        Ok(true)
                    } else if let Some(l) = &self.old_location
                        && l == &x.location
                    {
                        Ok(true)
                    } else {
                        Ok(false)
                    }
                } else {
                    Ok(false)
                }
            }
            _ => Err("mismatch type".into()),
        }
    }
}

adt!(
    Resource = Product | Cart | Order | Payment derive Debug, Clone, PartialEq
);

impl Resource {
    pub fn subtotal(&self) -> Option<Amount> {
        match self {
            Self::Product_(x) => Some(x.subtotal()),
            _ => None,
        }
    }
}

adt!(
    Product = SingleItem | BundleItems derive Debug, Clone, PartialEq with ProductFunc {
        fn subtotal(&self) -> Amount;
    }
);

#[derive(Debug, Clone, PartialEq)]
pub struct SingleItem {
    id: ItemId,
    unit_price: Amount,
    qty: Quantity,
}

#[derive(Debug, Clone, PartialEq)]
pub struct BundleItems {
    id: Option<BundleId>,
    items: Vec<Product>,
}

impl Product {
    pub fn new_single(id: ItemId, unit_price: Amount, qty: Quantity) -> Result<Self> {
        if qty < 1 {
            Err(format!("qty >= 1, qty={}", qty).into())
        } else if unit_price.is_negative() {
            Err(format!("unit_price >= 0, unit_price={}", unit_price).into())
        } else if id.is_empty() {
            error_empty_id()
        } else {
            Ok(SingleItem {
                id,
                unit_price,
                qty,
            }
            .into())
        }
    }

    pub fn new_bundle(id: Option<BundleId>, items: Vec<Self>) -> Result<Self> {
        if items.len() < 2 {
            Err(format!("must not be empty or single, item={}", items.len()).into())
        } else {
            Ok(BundleItems { id, items }.into())
        }
    }
}

impl ProductFunc for SingleItem {
    fn subtotal(&self) -> Amount {
        self.unit_price * self.qty
    }
}

impl ProductFunc for BundleItems {
    fn subtotal(&self) -> Amount {
        self.items
            .iter()
            .fold(Amount::default(), |acc, x| acc + x.subtotal())
    }
}

adt!(
    Movement = ChangeOwner | ChangeLocation | Consecutive | Promise | Exchange derive Debug, Clone
    with MovementFunc {
        fn latest_target(&self) -> Result<Vec<MoveResource>>;
        fn calc_cost(&self, target: &Location) -> Option<Amount>;
    }
);

#[derive(Debug, Clone)]
pub struct Move<T> {
    target: MoveResource,
    to: T,
    cost: Option<Amount>,
}

pub type ChangeOwner = Move<Owner>;
pub type ChangeLocation = Move<Location>;

#[derive(Debug, Clone)]
pub struct Consecutive(Box<Movement>, Box<Movement>);

#[derive(Debug, Clone)]
pub struct Promise(Box<Movement>);

#[derive(Debug, Clone)]
pub struct Exchange(Box<Movement>, Box<Movement>);

impl Movement {
    pub fn new_with_owner(target: MoveResource, to: Owner, cost: Option<Amount>) -> Result<Self> {
        if target.owner() == &to {
            error_no_move()
        } else {
            Ok(ChangeOwner { target, to, cost }.into())
        }
    }

    pub fn new_with_location(
        target: MoveResource,
        to: Location,
        cost: Option<Amount>,
    ) -> Result<Self> {
        if let Some(from) = target.location() {
            if from == &to {
                error_no_move()
            } else {
                Ok(ChangeLocation { target, to, cost }.into())
            }
        } else {
            Err("no location".into())
        }
    }

    pub fn cons(self, m: Movement) -> Self {
        Consecutive(Box::new(self), Box::new(m)).into()
    }

    pub fn promise(self) -> Self {
        Promise(Box::new(self)).into()
    }

    pub fn exchange(self, m: Movement) -> Self {
        Exchange(Box::new(self), Box::new(m)).into()
    }
}

impl MovementFunc for ChangeOwner {
    fn latest_target(&self) -> Result<Vec<MoveResource>> {
        let target = self.target.next_owner(self.to.clone())?;
        Ok(vec![target])
    }

    fn calc_cost(&self, _target: &Location) -> Option<Amount> {
        None
    }
}

impl MovementFunc for ChangeLocation {
    fn latest_target(&self) -> Result<Vec<MoveResource>> {
        let target = self.target.next_location(self.to.clone())?;
        Ok(vec![target])
    }

    fn calc_cost(&self, target: &Location) -> Option<Amount> {
        if self.to == *target { self.cost } else { None }
    }
}

impl MovementFunc for Consecutive {
    fn latest_target(&self) -> Result<Vec<MoveResource>> {
        let a = self.0.latest_target()?;
        let b = self.1.latest_target()?;

        if a.is_empty() {
            Ok(b)
        } else {
            let mut a = a;

            for x in b {
                if let Some((i, y)) = a
                    .iter()
                    .enumerate()
                    .find(|(_, r)| r.resource() == x.resource())
                    && x.is_next(y)?
                {
                    a.remove(i);
                }

                a.push(x);
            }

            Ok(a)
        }
    }

    fn calc_cost(&self, target: &Location) -> Option<Amount> {
        let a = self.0.calc_cost(target);
        let b = self.1.calc_cost(target);

        if a.is_none() {
            b
        } else {
            a.map(|x| x + b.unwrap_or_default())
        }
    }
}

impl MovementFunc for Promise {
    fn latest_target(&self) -> Result<Vec<MoveResource>> {
        todo!()
    }

    fn calc_cost(&self, target: &Location) -> Option<Amount> {
        todo!()
    }
}

impl MovementFunc for Exchange {
    fn latest_target(&self) -> Result<Vec<MoveResource>> {
        todo!()
    }

    fn calc_cost(&self, target: &Location) -> Option<Amount> {
        todo!()
    }
}

adt!(
    Owner = Unknown | System | Anonymous | User derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct Unknown;

#[derive(Debug, Clone, PartialEq)]
pub struct System;

#[derive(Debug, Clone, PartialEq)]
pub struct Anonymous;

#[derive(Debug, Clone, PartialEq)]
pub struct User(UserId);

adt!(
    Location = Unknown | Warehouse | Cart | Order | Address derive Debug, Clone, PartialEq
);

impl Location {
    pub fn eq_cart(&self, cart: &Cart) -> bool {
        match self {
            Self::Cart_(c) => c == cart,
            _ => false,
        }
    }
}

adt!(
    Warehouse = PhysicalWarehouse | LogicalWarehouse derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct PhysicalWarehouse(WarehouseId);

#[derive(Debug, Clone, PartialEq)]
pub struct LogicalWarehouse(WarehouseId);

impl Warehouse {
    pub fn new_physical(id: WarehouseId) -> Result<Self> {
        if id.is_empty() {
            error_empty_id()
        } else {
            Ok(PhysicalWarehouse(id).into())
        }
    }

    pub fn new_logical(id: WarehouseId) -> Result<Self> {
        if id.is_empty() {
            error_empty_id()
        } else {
            Ok(LogicalWarehouse(id).into())
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct Cart(CartId);

impl Cart {
    pub fn new(id: CartId) -> Result<Self> {
        if id.is_empty() {
            error_empty_id()
        } else {
            Ok(Cart(id))
        }
    }

    pub fn id(&self) -> &CartId {
        &self.0
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct Address {
    zip_code: String,
    address: String,
}

#[derive(Debug, Clone, PartialEq)]
pub struct Order(OrderId);

impl Order {
    pub fn new(id: OrderId) -> Result<Self> {
        if id.is_empty() {
            error_empty_id()
        } else {
            Ok(Order(id))
        }
    }

    pub fn id(&self) -> &OrderId {
        &self.0
    }
}

adt!(
    Payment = Credit | Emoney derive Debug, Clone, PartialEq with PaymentFunc {
        fn amount_billed(&self) -> Amount;
    }
);

#[derive(Debug, Clone, PartialEq)]
pub struct Credit {
    id: PaymentId,
    amount_billed: Amount,
}

#[derive(Debug, Clone, PartialEq)]
pub struct Emoney {
    id: PaymentId,
    amount_billed: Amount,
}

impl Payment {
    pub fn new_credit(id: PaymentId, billed: Amount) -> Result<Self> {
        if id.is_empty() {
            error_empty_id()
        } else if billed <= 0 {
            Err(format!("invalid amount billed, billed={}", billed).into())
        } else {
            Ok(Credit {
                id,
                amount_billed: billed,
            }
            .into())
        }
    }

    pub fn new_emoney(id: PaymentId, billed: Amount) -> Result<Self> {
        if id.is_empty() {
            error_empty_id()
        } else if billed <= 0 {
            Err(format!("invalid amount billed, billed={}", billed).into())
        } else {
            Ok(Emoney {
                id,
                amount_billed: billed,
            }
            .into())
        }
    }
}

impl PaymentFunc for Credit {
    fn amount_billed(&self) -> Amount {
        self.amount_billed
    }
}

impl PaymentFunc for Emoney {
    fn amount_billed(&self) -> Amount {
        self.amount_billed
    }
}

fn error_empty_id<T>() -> Result<T> {
    Err("id must not be empy".into())
}

fn error_not_supported<T>() -> Result<T> {
    Err("not supported".into())
}

fn error_no_move<T>() -> Result<T> {
    Err("no move, from is to".into())
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn next_owner() {
        let s = MoveResource::new(Cart("cart-1".into()).into(), System.into(), None);

        let r = s.next_owner(Anonymous.into());

        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(Owner::from(Anonymous), *r.owner());
        assert!(r.is_next(&s).unwrap());
    }

    #[test]
    fn is_next_other_owner() {
        let s = MoveResource::new(Cart("cart-1".into()).into(), System.into(), None);

        let r = s.next_owner(Anonymous.into()).unwrap();

        let s2 = MoveResource::new(Cart("cart-1".into()).into(), User("u1".into()).into(), None);

        assert_eq!(false, r.is_next(&s2).unwrap());
    }

    #[test]
    fn is_next_other_cartid() {
        let s = MoveResource::new(Cart("cart-1".into()).into(), System.into(), None);

        let r = s.next_owner(Anonymous.into()).unwrap();

        let s2 = MoveResource::new(Cart("cart-2".into()).into(), System.into(), None);

        assert_eq!(false, r.is_next(&s2).unwrap());
    }

    #[test]
    fn next_location() {
        let w = Warehouse::new_logical("stock-1".into()).unwrap();
        let p = Product::new_single("A-item".into(), 1500, 2).unwrap();

        let s = MoveResource::new(p.into(), System.into(), Some(w.into()));

        let r = s.next_location(Cart("cart-1".into()).into());

        assert!(r.is_ok());

        let r = r.unwrap();

        assert_eq!(Some(&Location::from(Cart("cart-1".into()))), r.location());
        assert!(r.is_next(&s).unwrap());
    }

    #[test]
    fn single_item_subtotal() {
        let p = Product::new_single("A1".into(), 1100, 3).unwrap();

        assert_eq!(3300, p.subtotal());
    }

    #[test]
    fn bundle_items_subtotal() {
        let items = vec![
            Product::new_single("A1".into(), 1100, 3).unwrap(),
            Product::new_single("B2".into(), 50, 1).unwrap(),
            Product::new_single("C3".into(), 220, 2).unwrap(),
        ];

        let p = Product::new_bundle(None, items).unwrap();

        assert_eq!(3790, p.subtotal());
    }

    #[test]
    fn resource_subtotal() {
        let r1: Resource = Cart("C1".into()).into();
        assert_eq!(None, r1.subtotal());

        let r2: Resource = Order("O2".into()).into();
        assert_eq!(None, r2.subtotal());

        let r3: Resource = Product::new_single("ITEM-1".into(), 550, 3).unwrap().into();
        assert_eq!(Some(1650), r3.subtotal());
    }
}
