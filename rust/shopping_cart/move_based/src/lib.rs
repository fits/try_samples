use macuru::adt;

pub type Quantity = isize;
pub type ItemId = String;
pub type WarehouseId = String;
pub type CartId = String;
pub type UserId = String;

#[derive(Debug, Clone)]
pub struct Move<T, I> {
    target: T,
    from: I,
    to: I,
    qty: Option<Quantity>,
}

pub type ChangeLocation = Move<Target, Location>;
pub type ChangeOwnership = Move<Target, Owner>;

adt!(
    Movement = ChangeLocation | ChangeOwnership | Serial derive Debug, Clone with MovementFunc {
        fn cart_items(&self, c: &Cart) -> Vec<(Item, Quantity)>;
        fn last_move(&self) -> Self;
    }
);

#[derive(Debug, Clone)]
pub struct Serial(Box<Movement>, Box<Movement>);

fn eq_cart_location(loc: &Location, c: &Cart) -> bool {
    if let Ok(x) = Cart::try_from(loc.clone()) {
        x == *c
    } else {
        false
    }
}

impl MovementFunc for ChangeOwnership {
    fn cart_items(&self, _c: &Cart) -> Vec<(Item, Quantity)> {
        Vec::new()
    }

    fn last_move(&self) -> Movement {
        self.clone().into()
    }
}

impl MovementFunc for ChangeLocation {
    fn cart_items(&self, c: &Cart) -> Vec<(Item, Quantity)> {
        if let Ok(x) = Item::try_from(self.target.clone()) {
            let mut q = 0;

            if eq_cart_location(&self.to, c) {
                q += self.qty.unwrap_or_default();
            }

            if eq_cart_location(&self.from, c) {
                q -= self.qty.unwrap_or_default();
            }

            if q != 0 { vec![(x, q)] } else { Vec::new() }
        } else {
            Vec::new()
        }
    }

    fn last_move(&self) -> Movement {
        self.clone().into()
    }
}

impl MovementFunc for Serial {
    fn cart_items(&self, c: &Cart) -> Vec<(Item, Quantity)> {
        let a = self.0.cart_items(c);
        let b = self.1.cart_items(c);

        if a.is_empty() {
            b
        } else {
            let mut a = a;

            for x in b {
                if let Some((i, _)) = a.iter().enumerate().find(|(_, y)| y.0 == x.0) {
                    a.get_mut(i).unwrap().1 += x.1;
                } else {
                    a.push(x)
                }
            }

            a
        }
    }

    fn last_move(&self) -> Movement {
        self.1.last_move()
    }
}

adt!(
    Target = Item | Cart derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct Item(ItemId);

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
    Location = Anywhere | Warehouse | Cart derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct Anywhere;

#[derive(Debug, Clone, PartialEq)]
pub struct Cart(CartId);

adt!(
    Warehouse = PhysicalWarehouse | LogicalWarehouse derive Debug, Clone, PartialEq
);

#[derive(Debug, Clone, PartialEq)]
pub struct PhysicalWarehouse(WarehouseId);

#[derive(Debug, Clone, PartialEq)]
pub struct LogicalWarehouse(WarehouseId);

#[derive(Debug, Clone)]
pub struct CartState {
    id: CartId,
    history: Movement,
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

impl CartState {
    pub fn new(cart_id: CartId, user: Owner) -> Result<Self> {
        if cart_id.is_empty() {
            Err("cart_id is emply".into())
        } else {
            let history = ChangeOwnership {
                target: Cart(cart_id.clone()).into(),
                from: System.into(),
                to: user.into(),
                qty: None,
            }
            .into();

            Ok(Self {
                id: cart_id,
                history,
            })
        }
    }

    pub fn items(&self) -> Vec<(Item, Quantity)> {
        self.history.cart_items(&Cart(self.id.clone()))
    }

    pub fn add_item(&self, item: Item, qty: Quantity) -> Result<Self> {
        if qty < 1 {
            Err("must be qty >= 1".into())
        } else {
            let Self { id, history } = self;

            let m: Movement = ChangeLocation {
                target: item.into(),
                from: Anywhere.into(),
                to: Cart(id.clone()).into(),
                qty: Some(qty),
            }
            .into();

            let new_history: Movement = Serial(Box::new(history.clone()), Box::new(m)).into();

            Ok(Self {
                id: id.clone(),
                history: new_history,
            })
        }
    }

    pub fn remove_item(&self, item: Item, qty: Quantity) -> Result<Self> {
        if qty < 1 {
            Err("must be qty >= 1".into())
        } else {
            let items = self.items();

            if let Some((_, q)) = items.iter().find(|x| x.0 == item) {
                if *q >= qty {
                    let Self { id, history } = self;

                    let m: Movement = ChangeLocation {
                        target: item.into(),
                        from: Cart(id.clone()).into(),
                        to: Anywhere.into(),
                        qty: Some(qty),
                    }
                    .into();

                    let new_history: Movement =
                        Serial(Box::new(history.clone()), Box::new(m)).into();

                    Ok(Self {
                        id: id.clone(),
                        history: new_history,
                    })
                } else {
                    Err(format!("over remove: current={}, remove={}", q, qty).into())
                }
            } else {
                Err(format!("not found item: id={}", item.0).into())
            }
        }
    }

    pub fn last_move(&self) -> Movement {
        self.history.last_move()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    fn serial(a: Movement, b: Movement) -> Movement {
        Serial(Box::new(a), Box::new(b)).into()
    }

    #[test]
    fn create_cart() {
        let c = CartState::new("cart-1".into(), Anonymous.into());

        assert!(c.is_ok());

        let c = c.unwrap();

        assert_eq!("cart-1", c.id);

        if let Ok(m) = ChangeOwnership::try_from(c.history.clone()) {
            assert_eq!(Owner::from(System), m.from);
            assert_eq!(Owner::from(Anonymous), m.to);
            assert!(m.qty.is_none());
        } else {
            assert!(false, "not change ownership");
        }
    }

    #[test]
    fn add_item_cart() {
        let c = CartState::new("cart-1".into(), Anonymous.into()).unwrap();

        let r = c.add_item(Item("A1".into()), 1);

        if let Ok(m) = r.map(|x| x.history) {
            let Serial(_, m) = Serial::try_from(m).unwrap();
            let m = ChangeLocation::try_from(m.as_ref().clone()).unwrap();

            assert_eq!(Target::from(Item("A1".into())), m.target);
            assert_eq!(Location::from(Anywhere), m.from);
            assert_eq!(Location::from(Cart("cart-1".into())), m.to);
            assert_eq!(Some(1), m.qty);
        } else {
            assert!(false, "failed add item")
        }
    }

    #[test]
    fn remove_item_cart() {
        let id = "cart-1".to_string();

        let c = CartState {
            id: id.clone(),
            history: serial(
                ChangeOwnership {
                    target: Cart(id.clone()).into(),
                    from: System.into(),
                    to: Anonymous.into(),
                    qty: None,
                }
                .into(),
                ChangeLocation {
                    target: Item("A1".into()).into(),
                    from: Anywhere.into(),
                    to: Cart(id.clone()).into(),
                    qty: Some(2),
                }
                .into(),
            ),
        };

        let r = c.remove_item(Item("A1".into()), 1);

        if let Ok(m) = r.map(|x| x.history) {
            let Serial(_, m) = Serial::try_from(m).unwrap();
            let m = ChangeLocation::try_from(m.as_ref().clone()).unwrap();

            assert_eq!(Target::from(Item("A1".into())), m.target);
            assert_eq!(Location::from(Cart("cart-1".into())), m.from);
            assert_eq!(Location::from(Anywhere), m.to);
            assert_eq!(Some(1), m.qty);
        } else {
            assert!(false, "failed remove item")
        }
    }

    #[test]
    fn remove_item_cart_multi_items() {
        let id = "cart-1".to_string();

        let c = CartState {
            id: id.clone(),
            history: serial(
                serial(
                    serial(
                        ChangeOwnership {
                            target: Cart(id.clone()).into(),
                            from: System.into(),
                            to: Anonymous.into(),
                            qty: None,
                        }
                        .into(),
                        ChangeLocation {
                            target: Item("A1".into()).into(),
                            from: Anywhere.into(),
                            to: Cart(id.clone()).into(),
                            qty: Some(2),
                        }
                        .into(),
                    ),
                    ChangeLocation {
                        target: Item("B2".into()).into(),
                        from: Anywhere.into(),
                        to: Cart(id.clone()).into(),
                        qty: Some(1),
                    }
                    .into(),
                ),
                ChangeLocation {
                    target: Item("A1".into()).into(),
                    from: Anywhere.into(),
                    to: Cart(id.clone()).into(),
                    qty: Some(3),
                }
                .into(),
            ),
        };

        let r = c.remove_item(Item("A1".into()), 5);

        if let Ok(m) = r.map(|x| x.history) {
            let Serial(_, m) = Serial::try_from(m).unwrap();
            let m = ChangeLocation::try_from(m.as_ref().clone()).unwrap();

            assert_eq!(Target::from(Item("A1".into())), m.target);
            assert_eq!(Location::from(Cart("cart-1".into())), m.from);
            assert_eq!(Location::from(Anywhere), m.to);
            assert_eq!(Some(5), m.qty);

            assert_eq!(1, m.cart_items(&Cart(id.clone())).len());
        } else {
            assert!(false, "failed remove item")
        }
    }

    #[test]
    fn remove_item_cart_over() {
        let id = "cart-1".to_string();

        let c = CartState {
            id: id.clone(),
            history: serial(
                ChangeOwnership {
                    target: Cart(id.clone()).into(),
                    from: System.into(),
                    to: Anonymous.into(),
                    qty: None,
                }
                .into(),
                ChangeLocation {
                    target: Item("A1".into()).into(),
                    from: Anywhere.into(),
                    to: Cart(id.clone()).into(),
                    qty: Some(2),
                }
                .into(),
            ),
        };

        let r = c.remove_item(Item("A1".into()), 3);

        assert!(r.is_err());
    }

    #[test]
    fn remove_not_exist_item_cart() {
        let id = "cart-1".to_string();

        let c = CartState {
            id: id.clone(),
            history: serial(
                ChangeOwnership {
                    target: Cart(id.clone()).into(),
                    from: System.into(),
                    to: Anonymous.into(),
                    qty: None,
                }
                .into(),
                ChangeLocation {
                    target: Item("A1".into()).into(),
                    from: Anywhere.into(),
                    to: Cart(id.clone()).into(),
                    qty: Some(2),
                }
                .into(),
            ),
        };

        let r = c.remove_item(Item("B2".into()), 1);

        assert!(r.is_err());
    }

    #[test]
    fn cart_items_changeownership() {
        let m: Movement = ChangeOwnership {
            target: Cart("c1".into()).into(),
            from: System.into(),
            to: Anonymous.into(),
            qty: None,
        }
        .into();

        let r = m.cart_items(&Cart("c1".into()));

        assert!(r.is_empty());
    }

    #[test]
    fn cart_items_changelocation_to_cart() {
        let m: Movement = ChangeLocation {
            target: Item("A1".into()).into(),
            from: Anywhere.into(),
            to: Cart("cart-1".into()).into(),
            qty: Some(2),
        }
        .into();

        let r = m.cart_items(&Cart("cart-1".into()));

        assert_eq!(1, r.len());

        let i1 = r.first().unwrap();
        assert_eq!("A1", i1.0.0);
        assert_eq!(2, i1.1);
    }

    #[test]
    fn cart_items_changelocation_to_other_cart() {
        let m: Movement = ChangeLocation {
            target: Item("A1".into()).into(),
            from: Anywhere.into(),
            to: Cart("cart-1".into()).into(),
            qty: Some(2),
        }
        .into();

        let r = m.cart_items(&Cart("cart-2".into()));

        assert_eq!(0, r.len());
    }

    #[test]
    fn cart_items_changelocation_from_cart() {
        let m: Movement = ChangeLocation {
            target: Item("A1".into()).into(),
            from: Cart("cart-1".into()).into(),
            to: Anywhere.into(),
            qty: Some(2),
        }
        .into();

        let r = m.cart_items(&Cart("cart-1".into()));

        assert_eq!(1, r.len());

        let i1 = r.first().unwrap();
        assert_eq!("A1", i1.0.0);
        assert_eq!(-2, i1.1);
    }

    #[test]
    fn cart_items_changelocation_from_to_same() {
        let m: Movement = ChangeLocation {
            target: Item("A1".into()).into(),
            from: Cart("cart-1".into()).into(),
            to: Cart("cart-1".into()).into(),
            qty: Some(2),
        }
        .into();

        let r = m.cart_items(&Cart("cart-1".into()));

        assert_eq!(0, r.len());
    }

    #[test]
    fn items_single() {
        let id = "cart-2".to_string();

        let history: Movement = serial(
            ChangeOwnership {
                target: Cart(id.clone()).into(),
                from: System.into(),
                to: Anonymous.into(),
                qty: None,
            }
            .into(),
            ChangeLocation {
                target: Item("A1".into()).into(),
                from: Anywhere.into(),
                to: Cart(id.clone()).into(),
                qty: Some(2),
            }
            .into(),
        );

        let c = CartState { id, history };

        let r = c.items();

        assert_eq!(1, r.len());

        let i1 = r.first().unwrap();

        assert_eq!("A1", i1.0.0);
        assert_eq!(2, i1.1);
    }

    #[test]
    fn items_multi() {
        let id = "cart-2".to_string();

        let history: Movement = serial(
            serial(
                serial(
                    ChangeOwnership {
                        target: Cart(id.clone()).into(),
                        from: System.into(),
                        to: Anonymous.into(),
                        qty: None,
                    }
                    .into(),
                    ChangeLocation {
                        target: Item("A1".into()).into(),
                        from: Anywhere.into(),
                        to: Cart(id.clone()).into(),
                        qty: Some(2),
                    }
                    .into(),
                ),
                ChangeLocation {
                    target: Item("B2".into()).into(),
                    from: Anywhere.into(),
                    to: Cart(id.clone()).into(),
                    qty: Some(1),
                }
                .into(),
            ),
            ChangeLocation {
                target: Item("A1".into()).into(),
                from: Anywhere.into(),
                to: Cart(id.clone()).into(),
                qty: Some(3),
            }
            .into(),
        );

        let c = CartState { id, history };

        let r = c.items();

        assert_eq!(2, r.len());

        let i1 = r.first().unwrap();

        assert_eq!("A1", i1.0.0);
        assert_eq!(5, i1.1);

        let i2 = r.last().unwrap();

        assert_eq!("B2", i2.0.0);
        assert_eq!(1, i2.1);
    }

    #[test]
    fn last_move_changeownership() {
        let id = "cart1".to_string();

        let m: Movement = ChangeOwnership {
            target: Cart(id.clone()).into(),
            from: System.into(),
            to: Anonymous.into(),
            qty: None,
        }
        .into();

        let r = m.last_move();

        if let Ok(x) = ChangeOwnership::try_from(r) {
            assert_eq!(Target::from(Cart(id.clone())), x.target);
            assert_eq!(Owner::from(System), x.from);
            assert_eq!(Owner::from(Anonymous), x.to);
            assert!(x.qty.is_none());
        } else {
            assert!(false, "invalid last_move")
        }
    }

    #[test]
    fn last_move_changelocation() {
        let id = "cart1".to_string();

        let m: Movement = ChangeLocation {
            target: Item("A1".into()).into(),
            from: Anywhere.into(),
            to: Cart(id.clone()).into(),
            qty: Some(2),
        }
        .into();

        let r = m.last_move();

        if let Ok(x) = ChangeLocation::try_from(r) {
            assert_eq!(Target::from(Item("A1".into())), x.target);
            assert_eq!(Location::from(Anywhere), x.from);
            assert_eq!(Location::from(Cart(id.clone())), x.to);
            assert_eq!(Some(2), x.qty);
        } else {
            assert!(false, "invalid last_move")
        }
    }

    #[test]
    fn last_move_serial() {
        let id = "cart1".to_string();

        let m: Movement = serial(
            serial(
                serial(
                    ChangeOwnership {
                        target: Cart(id.clone()).into(),
                        from: System.into(),
                        to: Anonymous.into(),
                        qty: None,
                    }
                    .into(),
                    ChangeLocation {
                        target: Item("A1".into()).into(),
                        from: Anywhere.into(),
                        to: Cart(id.clone()).into(),
                        qty: Some(2),
                    }
                    .into(),
                ),
                ChangeLocation {
                    target: Item("B2".into()).into(),
                    from: Anywhere.into(),
                    to: Cart(id.clone()).into(),
                    qty: Some(1),
                }
                .into(),
            ),
            ChangeLocation {
                target: Item("A1".into()).into(),
                from: Anywhere.into(),
                to: Cart(id.clone()).into(),
                qty: Some(3),
            }
            .into(),
        );

        let r = m.last_move();

        if let Ok(x) = ChangeLocation::try_from(r) {
            assert_eq!(Target::from(Item("A1".into())), x.target);
            assert_eq!(Location::from(Anywhere), x.from);
            assert_eq!(Location::from(Cart(id.clone())), x.to);
            assert_eq!(Some(3), x.qty);
        } else {
            assert!(false, "invalid last_move")
        }
    }

    #[test]
    fn last_move_from_cart() {
        let id = "cart-1".to_string();

        let c = CartState {
            id: id.clone(),
            history: serial(
                serial(
                    serial(
                        ChangeOwnership {
                            target: Cart(id.clone()).into(),
                            from: System.into(),
                            to: Anonymous.into(),
                            qty: None,
                        }
                        .into(),
                        ChangeLocation {
                            target: Item("A1".into()).into(),
                            from: Anywhere.into(),
                            to: Cart(id.clone()).into(),
                            qty: Some(2),
                        }
                        .into(),
                    ),
                    ChangeLocation {
                        target: Item("B2".into()).into(),
                        from: Anywhere.into(),
                        to: Cart(id.clone()).into(),
                        qty: Some(1),
                    }
                    .into(),
                ),
                ChangeLocation {
                    target: Item("A1".into()).into(),
                    from: Anywhere.into(),
                    to: Cart(id.clone()).into(),
                    qty: Some(3),
                }
                .into(),
            ),
        };

        let r = c.last_move();

        if let Ok(x) = ChangeLocation::try_from(r) {
            assert_eq!(Target::from(Item("A1".into())), x.target);
            assert_eq!(Location::from(Anywhere), x.from);
            assert_eq!(Location::from(Cart("cart-1".into())), x.to);
            assert_eq!(Some(3), x.qty);
        } else {
            assert!(false, "failed last move")
        }
    }
}
