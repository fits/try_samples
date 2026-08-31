use macuru::adt;

pub type Amount = i64;
pub type Quantity = i32;
pub type UserId = String;
pub type CartId = String;
pub type WarehouseId = String;
pub type ItemId = String;
pub type BundleId = String;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

adt!(
    MoveResource = Owned | Placed derive Debug, Clone
);

#[derive(Debug, Clone)]
pub struct Owned {
    resource: Resource,
    owner: Owner,
}

#[derive(Debug, Clone)]
pub struct Placed {
    resource: Resource,
    owner: Owner,
    location: Location,
}

adt!(
    Resource = Product | Cart derive Debug, Clone
);

adt!(
    Product = SingleItem | BundleItems derive Debug, Clone
);

#[derive(Debug, Clone)]
pub struct SingleItem {
    id: ItemId,
    unit_price: Amount,
    qty: Quantity,
}

#[derive(Debug, Clone)]
pub struct BundleItems {
    id: Option<BundleId>,
    items: Vec<Product>,
}

adt!(
    Movement = ChangeOwner | ChangeLocation | Consecutive | Promise | Exchange derive Debug, Clone
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
    pub fn cons(a: Movement, b: Movement) -> Self {
        Consecutive(Box::new(a), Box::new(b)).into()
    }

    pub fn promise(a: Movement) -> Self {
        Promise(Box::new(a)).into()
    }

    pub fn exchange(a: Movement, b: Movement) -> Self {
        Exchange(Box::new(a), Box::new(b)).into()
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
    Location = Unknown | Warehouse | Cart | Address derive Debug, Clone, PartialEq
);

adt!(
    Warehouse = PhysicalWarehouse | LogicalWarehouse derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct PhysicalWarehouse(WarehouseId);

#[derive(Debug, Clone, PartialEq)]
pub struct LogicalWarehouse(WarehouseId);

impl Warehouse {
    pub fn physical(id: WarehouseId) -> Result<Self> {
        if id.is_empty() {
            Err("id must not be empty".into())
        } else {
            Ok(PhysicalWarehouse(id).into())
        }
    }

    pub fn logical(id: WarehouseId) -> Result<Self> {
        if id.is_empty() {
            Err("id must not be empty".into())
        } else {
            Ok(LogicalWarehouse(id).into())
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct Cart(CartId);

#[derive(Debug, Clone, PartialEq)]
pub struct Address {
    zip_code: String,
    address: String,
}

#[cfg(test)]
mod tests {
    use super::*;
}