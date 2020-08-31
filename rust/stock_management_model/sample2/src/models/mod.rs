
pub type ItemCode = String;
pub type LocationCode = String;
pub type Quantity = u32;

pub trait Event<S> {
    fn apply_to(&self, state: S) -> S;
}

#[allow(dead_code)]
#[derive(Debug, Clone, PartialEq)]
pub enum StockMoveEvent {
    Started {
        item: ItemCode, 
        qty: Quantity, 
        from: LocationCode, 
        to: LocationCode,
    },
    Completed,
    Cancelled,
    Assigned {
        item: ItemCode, 
        from: LocationCode,
        assigned: Quantity, 
    },
    Shipped {
        item: ItemCode, 
        from: LocationCode,
        outgoing: Quantity, 
    },
    AssignShipped {
        item: ItemCode, 
        from: LocationCode,
        outgoing: Quantity,
        assigned: Quantity,
    },
    Arrived {
        item: ItemCode, 
        to: LocationCode,
        incoming: Quantity, 
    },
}

#[allow(dead_code)]
#[derive(Debug, Clone)]
pub enum Stock {
    Unmanaged { item: ItemCode, location: LocationCode },
    Managed { 
        item: ItemCode, 
        location: LocationCode, 
        qty: Quantity, 
        assigned: Quantity
    },
}

#[allow(dead_code)]
impl Stock {
    pub fn managed_new(item: ItemCode, location: LocationCode) -> Self {
        Self::Managed { item, location, qty: 0, assigned: 0 }
    }

    pub fn unmanaged_new(item: ItemCode, location: LocationCode) -> Self {
        Self::Unmanaged { item, location }
    }

    pub fn eq_id(&self, item: &ItemCode, location: &LocationCode) -> bool {
        match self {
            Self::Managed { item: it, location: loc, .. } | 
            Self::Unmanaged { item: it, location: loc } => 
                it == item && loc == location
        }
    }

    pub fn is_sufficient(&self, v: Quantity) -> bool {
        match self {
            Self::Managed { qty, assigned, .. } =>
                v + assigned <= *qty,
            Self::Unmanaged { .. } => true, 
        }
    }

    fn update(&self, qty: Quantity, assigned: Quantity) -> Self {
        match self {
            Self::Managed { item, location, .. } => {
                Self::Managed {
                    item: item.clone(),
                    location: location.clone(),
                    qty,
                    assigned,
                }
            },
            Self::Unmanaged { .. } => self.clone(),
        }
    }

    pub(super) fn update_qty(&self, qty: Quantity) -> Self {
        match self {
            Self::Managed { assigned, .. } => self.update(qty, *assigned),
            Self::Unmanaged { .. } => self.clone(),
        }
    }

    fn update_assigned(&self, assigned: Quantity) -> Self {
        match self {
            Self::Managed { qty, .. } => self.update(*qty, assigned),
            Self::Unmanaged { .. } => self.clone(),
        }
    }
}

#[derive(Debug, Default, Clone, PartialEq)]
pub struct StockMoveInfo {
    item: ItemCode,
    qty: Quantity,
    from: LocationCode,
    to: LocationCode,
}

#[allow(dead_code)]
#[derive(Debug, Clone, PartialEq)]
pub enum StockMove {
    Nothing,
    Draft { info: StockMoveInfo },
    Completed { info: StockMoveInfo },
    Cancelled { info: StockMoveInfo },
    Assigned { info: StockMoveInfo, assigned: Quantity },
    Shipped { info: StockMoveInfo, outgoing: Quantity },
    Arrived { info: StockMoveInfo, incoming: Quantity },
    AssignFailed { info: StockMoveInfo },
    ShipmentFailed { info: StockMoveInfo },
}

type StockMoveResult = Option<(StockMove, StockMoveEvent)>;
type FindStock = fn(&ItemCode, &LocationCode) -> Option<Stock>;

#[allow(dead_code)]
impl StockMove {
    pub fn start(&self, item: ItemCode, qty: Quantity, 
        from: LocationCode, to: LocationCode) -> StockMoveResult {

        if qty < 1 {
            return None
        }

        let event = StockMoveEvent::Started {
            item: item.clone(), 
            qty: qty, 
            from: from.clone(), 
            to: to.clone()
        };

        self.apply_event(event)
    }

    pub fn complete(&self) -> StockMoveResult {
        self.apply_event(StockMoveEvent::Completed)
    }

    pub fn cancel(&self) -> StockMoveResult {
        self.apply_event(StockMoveEvent::Cancelled)
    }

    pub fn assign(&self, find_stock: FindStock) -> StockMoveResult {
        let info = self.info();

        if let Some(info) = info {
            find_stock(&info.item, &info.from)
                .and_then(|s| {
                    let assigned = if s.is_sufficient(info.qty) {
                        info.qty
                    } else {
                        0
                    };

                    self.apply_event(
                        StockMoveEvent::Assigned {
                            item: info.item.clone(),
                            from: info.from.clone(),
                            assigned,
                        }
                    )
                })
        } else {
            None
        }
    }

    pub fn ship(&self, outgoing: Quantity) -> StockMoveResult {
        let ev = match self {
            Self::Assigned { info, assigned } => 
                Some(StockMoveEvent::AssignShipped {
                    item: info.item.clone(),
                    from: info.from.clone(),
                    outgoing,
                    assigned: assigned.clone(),
                }),
            _ =>
                self.info()
                    .map(|i|
                        StockMoveEvent::Shipped {
                            item: i.item.clone(),
                            from: i.from.clone(),
                            outgoing,
                        }
                    ),
        };

        ev.and_then(|e| self.apply_event(e))
    }

    pub fn arrive(&self, incoming: Quantity) -> StockMoveResult {
        self.info()
            .and_then(|i|
                self.apply_event(StockMoveEvent::Arrived {
                    item: i.item.clone(),
                    to: i.to.clone(),
                    incoming,
                })
            )
    }

    fn info(&self) -> Option<StockMoveInfo> {
        match self {
            Self::Draft { info } |
            Self::Completed { info } |
            Self::Cancelled { info } |
            Self::Assigned { info, .. } |
            Self::AssignFailed { info, .. } |
            Self::Shipped { info, .. } |
            Self::ShipmentFailed { info } |
            Self::Arrived { info, .. } =>
                Some(info.clone()),
            Self::Nothing => None,
        }
    }

    fn apply_event(&self, event: StockMoveEvent) -> StockMoveResult {
        let new_state = event.apply_to(self.clone());

        Some((new_state, event))
            .filter(|r| r.0 != *self)
    }
}

impl Event<StockMove> for StockMoveEvent {
    fn apply_to(&self, state: StockMove) -> StockMove {
        match self {
            Self::Started { item, qty, from, to } => 
                if state == StockMove::Nothing {
                    StockMove::Draft {
                        info: StockMoveInfo { 
                            item: item.clone(), 
                            qty: qty.clone(), 
                            from: from.clone(), 
                            to: to.clone(),
                        }
                    }
                } else {
                    state
                },
            Self::Completed => 
                if let StockMove::Arrived { info, .. } = state {
                    StockMove::Completed { info: info.clone() }
                } else {
                    state
                },
            Self::Cancelled => 
                if let StockMove::Draft { info } = state {
                    StockMove::Cancelled { info: info.clone() }
                } else {
                    state
                },
            Self::Assigned { item, from, assigned } => 
                match state {
                    StockMove::Draft { info } 
                    if info.item == *item && info.from == *from => 
                        if *assigned > 0 {
                            StockMove::Assigned { 
                                info: info.clone(), 
                                assigned: assigned.clone(),
                            }
                        } else {
                            StockMove::AssignFailed { info: info.clone() }
                        },
                    _ => state,
                },
            Self::Shipped { item, from, outgoing } => 
                match state {
                    StockMove::Draft { info }
                    if info.item == *item && info.from == *from => 
                        if *outgoing > 0 {
                            StockMove::Shipped { 
                                info: info.clone(), 
                                outgoing: outgoing.clone(),
                            }
                        } else {
                            StockMove::ShipmentFailed { info: info.clone() }
                        }
                    _ => state,
                },
            Self::AssignShipped { item, from, outgoing, .. } => 
                match state {
                    StockMove::Assigned { info, .. }
                    if info.item == *item && info.from == *from => 
                        if *outgoing > 0 {
                            StockMove::Shipped { 
                                info: info.clone(), 
                                outgoing: outgoing.clone(),
                            }
                        } else {
                            StockMove::ShipmentFailed { info: info.clone() }
                        }
                    _ => state,
                },
            Self::Arrived { item, to, incoming } => 
                match state {
                    StockMove::Draft { info } | StockMove::Shipped { info, .. }
                    if info.item == *item && info.to == *to => 
                        StockMove::Arrived {
                            info: info.clone(),
                            incoming: incoming.clone(),
                        },
                    _ => state,
                },
        }
    }
}


#[test]
fn eq_id_managed() {
    let s = Stock::managed_new("item-1".to_string(), "loc-1".to_string());

    let item1 = "item-1".to_string();
    let loc1 = "loc-1".to_string();

    assert!(s.eq_id(&item1, &loc1));
    assert_eq!(s.eq_id(&item1, &"".to_string()), false);
    assert_eq!(s.eq_id(&"".to_string(), &loc1), false);
}

#[test]
fn eq_id_unmanaged() {
    let s = Stock::unmanaged_new("item-1".to_string(), "loc-1".to_string());

    let item1 = "item-1".to_string();
    let loc1 = "loc-1".to_string();

    assert!(s.eq_id(&item1, &loc1));
    assert_eq!(s.eq_id(&item1, &"".to_string()), false);
    assert_eq!(s.eq_id(&"".to_string(), &loc1), false);
}

#[test]
fn update_qty_managed() {
    let s = Stock::managed_new("item-1".to_string(), "loc-1".to_string());

    let r = s.update_qty(5);

    if let Stock::Managed { qty, assigned, .. } = r {
        assert_eq!(qty, 5);
        assert_eq!(assigned, 0);
    } else {
        assert!(false);
    }
}

#[test]
fn update_assigned_managed() {
    let s = Stock::managed_new("item-1".to_string(), "loc-1".to_string());

    let r = s.update_assigned(1);

    if let Stock::Managed { qty, assigned, .. } = r {
        assert_eq!(qty, 0);
        assert_eq!(assigned, 1);
    } else {
        assert!(false);
    }
}

#[test]
fn update_qty_assigned_managed() {
    let s = Stock::managed_new("item-1".to_string(), "loc-1".to_string());

    let s2 = s.update_qty(5);
    let r1 = s2.update_assigned(2);

    if let Stock::Managed { qty, assigned, .. } = r1 {
        assert_eq!(qty, 5);
        assert_eq!(assigned, 2);
    } else {
        assert!(false);
    }

    let r2 = r1.update(6, 3);

    if let Stock::Managed { qty, assigned, .. } = r2 {
        assert_eq!(qty, 6);
        assert_eq!(assigned, 3);
    } else {
        assert!(false);
    }
}

#[test]
fn is_sufficient_managed() {
    let s = Stock::managed_new("item-1".to_string(), "loc-1".to_string());
    assert_eq!(s.is_sufficient(1), false);

    let s2 = s.update(5, 2);
    assert!(s2.is_sufficient(0));
    assert!(s2.is_sufficient(1));
    assert!(s2.is_sufficient(3));
    assert_eq!(s2.is_sufficient(4), false);
}

#[test]
fn start_nothing() {
    let state = StockMove::Nothing;

    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let r = state.start(item1.clone(), 1, from1.clone(), to1.clone());

    if let Some((s, e)) = r {
        if let StockMove::Draft { info } = s {
            assert_eq!(info.item, item1);
            assert_eq!(info.qty, 1);
            assert_eq!(info.from, from1);
            assert_eq!(info.to, to1);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::Started { item, qty, from, to } = e {
            assert_eq!(item, item1);
            assert_eq!(qty, 1);
            assert_eq!(from, from1);
            assert_eq!(to, to1);
        } else {
            assert!(false);
        }

    } else {
        assert!(false);
    }
}

#[test]
fn start_nothing_with_zero_qty() {
    let state = StockMove::Nothing;

    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let r = state.start(item1.clone(), 0, from1.clone(), to1.clone());
    assert!(r == None);
}

#[test]
fn start_not_nothing() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Draft {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 1,
            from: from1.clone(),
            to: to1.clone(),
        },
    };

    let r = state.start(item1.clone(), 1, from1.clone(), to1.clone());
    assert!(r == None);
}

#[test]
fn assign_after_draft_with_stock() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Draft {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 1,
            from: from1.clone(),
            to: to1.clone(),
        },
    };

    let r = state.assign(|item, from| {
        let stock = Stock::managed_new(item.clone(), from.clone());
        Some(stock.update_qty(5))
    });

    if let Some((s, e)) = r {
        if let StockMove::Assigned { assigned, .. } = s {
            assert_eq!(assigned, 1);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::Assigned { item, from, assigned } = e {
            assert_eq!(item, item1);
            assert_eq!(from, from1);
            assert_eq!(assigned, 1);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}

#[test]
fn assign_after_draft_with_zero_stock() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Draft {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 1,
            from: from1.clone(),
            to: to1.clone(),
        },
    };

    let r = state.assign(|item, from|
        Some(Stock::managed_new(item.clone(), from.clone()))
    );

    if let Some((s, e)) = r {
        if let StockMove::AssignFailed { .. } = s {
            assert!(true);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::Assigned { item, from, assigned } = e {
            assert_eq!(item, item1);
            assert_eq!(from, from1);
            assert_eq!(assigned, 0);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}

#[test]
fn assign_after_draft_with_no_stock() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Draft {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 1,
            from: from1.clone(),
            to: to1.clone(),
        },
    };

    let r = state.assign(|_, _| None);

    assert!(r == None);
}

#[test]
fn ship_after_draft() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Draft {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 1,
            from: from1.clone(),
            to: to1.clone(),
        },
    };

    let r = state.ship(1);

    if let Some((s, e)) = r {
        if let StockMove::Shipped { outgoing, .. } = s {
            assert_eq!(outgoing, 1);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::Shipped { item, from, outgoing } = e {
            assert_eq!(item, item1);
            assert_eq!(from, from1);
            assert_eq!(outgoing, 1);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}

#[test]
fn ship_after_draft_with_zero_outgoing() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Draft {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 1,
            from: from1.clone(),
            to: to1.clone(),
        },
    };

    let r = state.ship(0);

    if let Some((s, e)) = r {
        if let StockMove::ShipmentFailed { .. } = s {
            assert!(true);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::Shipped { item, from, outgoing } = e {
            assert_eq!(item, item1);
            assert_eq!(from, from1);
            assert_eq!(outgoing, 0);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}

#[test]
fn ship_after_assigned() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Assigned {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 2,
            from: from1.clone(),
            to: to1.clone(),
        },
        assigned: 2,
    };

    let r = state.ship(1);

    if let Some((s, e)) = r {
        if let StockMove::Shipped { outgoing, .. } = s {
            assert_eq!(outgoing, 1);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::AssignShipped { item, from, outgoing, assigned } = e {
            assert_eq!(item, item1);
            assert_eq!(from, from1);
            assert_eq!(outgoing, 1);
            assert_eq!(assigned, 2);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}

#[test]
fn ship_after_assigned_with_zero_outgoing() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Assigned {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 2,
            from: from1.clone(),
            to: to1.clone(),
        },
        assigned: 2,
    };

    let r = state.ship(0);

    if let Some((s, e)) = r {
        if let StockMove::ShipmentFailed { .. } = s {
            assert!(true);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::AssignShipped { item, from, outgoing, assigned } = e {
            assert_eq!(item, item1);
            assert_eq!(from, from1);
            assert_eq!(outgoing, 0);
            assert_eq!(assigned, 2);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}

#[test]
fn arrive_after_shipped() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Shipped {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 2,
            from: from1.clone(),
            to: to1.clone(),
        },
        outgoing: 2,
    };

    let r = state.arrive(2);

    if let Some((s, e)) = r {
        if let StockMove::Arrived { incoming, .. } = s {
            assert_eq!(incoming, 2);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::Arrived { item, to, incoming } = e {
            assert_eq!(item, item1);
            assert_eq!(to, to1);
            assert_eq!(incoming, 2);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}

#[test]
fn arrive_after_draft() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Draft {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 2,
            from: from1.clone(),
            to: to1.clone(),
        },
    };

    let r = state.arrive(2);

    if let Some((s, e)) = r {
        if let StockMove::Arrived { incoming, .. } = s {
            assert_eq!(incoming, 2);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::Arrived { item, to, incoming } = e {
            assert_eq!(item, item1);
            assert_eq!(to, to1);
            assert_eq!(incoming, 2);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}

#[test]
fn arrive_after_shipped_with_zero_incoming() {
    let item1 = "item-1".to_string();
    let from1 = "from-1".to_string();
    let to1 = "to-1".to_string();

    let state = StockMove::Shipped {
        info: StockMoveInfo {
            item: item1.clone(),
            qty: 2,
            from: from1.clone(),
            to: to1.clone(),
        },
        outgoing: 2,
    };

    let r = state.arrive(0);

    if let Some((s, e)) = r {
        if let StockMove::Arrived { incoming, .. } = s {
            assert_eq!(incoming, 0);
        } else {
            assert!(false);
        }

        if let StockMoveEvent::Arrived { item, to, incoming } = e {
            assert_eq!(item, item1);
            assert_eq!(to, to1);
            assert_eq!(incoming, 0);
        } else {
            assert!(false);
        }
    } else {
        assert!(false);
    }
}
