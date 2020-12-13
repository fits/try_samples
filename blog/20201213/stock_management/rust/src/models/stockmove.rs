
use std::slice;

use super::events::StockMoveEvent;

pub type ItemCode = String;
pub type LocationCode = String;
pub type Quantity = u32;

pub trait Event<S> {
    type Output;

    fn apply_to(&self, state: S) -> Self::Output;
}

pub trait Restore<E> {
    fn restore(self, events: slice::Iter<E>) -> Self;
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

    fn update_qty(&self, qty: Quantity) -> Self {
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
    Completed { info: StockMoveInfo, outgoing: Quantity, incoming: Quantity },
    Cancelled { info: StockMoveInfo },
    Assigned { info: StockMoveInfo, assigned: Quantity },
    Shipped { info: StockMoveInfo, outgoing: Quantity },
    Arrived { info: StockMoveInfo, outgoing: Quantity, incoming: Quantity },
    AssignFailed { info: StockMoveInfo },
    ShipmentFailed { info: StockMoveInfo },
}

type MoveEvent = StockMoveEvent<ItemCode, LocationCode, Quantity>;
type MoveResult = Option<(StockMove, MoveEvent)>;

#[allow(dead_code)]
impl StockMove {
    pub fn initial_state() -> Self {
        Self::Nothing
    }

    pub fn start(&self, item: ItemCode, qty: Quantity, 
        from: LocationCode, to: LocationCode) -> MoveResult {

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

    pub fn complete(&self) -> MoveResult {
        self.apply_event(StockMoveEvent::Completed)
    }

    pub fn cancel(&self) -> MoveResult {
        self.apply_event(StockMoveEvent::Cancelled)
    }

    pub fn assign(&self, stock: &Stock) -> MoveResult {
        if let Some(info) = self.info() {
            if stock.eq_id(&info.item, &info.from) {
                let assigned = if stock.is_sufficient(info.qty) {
                    info.qty
                } else {
                    0
                };

                return self.apply_event(
                    StockMoveEvent::Assigned {
                        item: info.item.clone(),
                        from: info.from.clone(),
                        assigned,
                    }
                )
            }
        }

        None
    }

    pub fn ship(&self, outgoing: Quantity) -> MoveResult {
        let ev = match self {
            Self::Assigned { info, assigned } => {
                Some(StockMoveEvent::AssignShipped {
                    item: info.item.clone(),
                    from: info.from.clone(),
                    outgoing,
                    assigned: assigned.clone(),
                })
            },
            _ => {
                self.info()
                    .map(|i|
                        StockMoveEvent::Shipped {
                            item: i.item.clone(),
                            from: i.from.clone(),
                            outgoing,
                        }
                    )
            },
        };

        ev.and_then(|e| self.apply_event(e))
    }

    pub fn arrive(&self, incoming: Quantity) -> MoveResult {
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
            Self::Completed { info, .. } |
            Self::Cancelled { info } |
            Self::Assigned { info, .. } |
            Self::AssignFailed { info, .. } |
            Self::Shipped { info, .. } |
            Self::ShipmentFailed { info } |
            Self::Arrived { info, .. } => {
                Some(info.clone())
            },
            Self::Nothing => None,
        }
    }

    fn apply_event(&self, event: MoveEvent) -> MoveResult {
        let new_state = event.apply_to(self.clone());

        Some((new_state, event))
            .filter(|r| r.0 != *self)
    }
}

impl Event<StockMove> for MoveEvent {
    type Output = StockMove;

    fn apply_to(&self, state: StockMove) -> Self::Output {
        match self {
            Self::Started { item, qty, from, to } => {
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
                }
            },
            Self::Completed => {
                if let StockMove::Arrived { info, outgoing, incoming } = state {
                    StockMove::Completed { info: info.clone(), outgoing, incoming }
                } else {
                    state
                }
            },
            Self::Cancelled => {
                if let StockMove::Draft { info } = state {
                    StockMove::Cancelled { info: info.clone() }
                } else {
                    state
                }
            },
            Self::Assigned { item, from, assigned } => {
                match state {
                    StockMove::Draft { info } 
                    if info.item == *item && info.from == *from => {

                        if *assigned > 0 {
                            StockMove::Assigned { 
                                info: info.clone(), 
                                assigned: assigned.clone(),
                            }
                        } else {
                            StockMove::AssignFailed { info: info.clone() }
                        }
                    },
                    _ => state,
                }
            },
            Self::Shipped { item, from, outgoing } => {
                match state {
                    StockMove::Draft { info }
                    if info.item == *item && info.from == *from => {

                        if *outgoing > 0 {
                            StockMove::Shipped { 
                                info: info.clone(), 
                                outgoing: outgoing.clone(),
                            }
                        } else {
                            StockMove::ShipmentFailed { info: info.clone() }
                        }
                    },
                    _ => state,
                }
            },
            Self::AssignShipped { item, from, outgoing, .. } => {
                match state {
                    StockMove::Assigned { info, .. }
                    if info.item == *item && info.from == *from => {

                        if *outgoing > 0 {
                            StockMove::Shipped { 
                                info: info.clone(), 
                                outgoing: outgoing.clone(),
                            }
                        } else {
                            StockMove::ShipmentFailed { info: info.clone() }
                        }
                    },
                    _ => state,
                }
            },
            Self::Arrived { item, to, incoming } => {
                match state {
                    StockMove::Draft { info }
                    if info.item == *item && info.to == *to => {
                        StockMove::Arrived {
                            info: info.clone(),
                            outgoing: 0,
                            incoming: *incoming,
                        }
                    },
                    StockMove::Shipped { info, outgoing }
                    if info.item == *item && info.to == *to => {
                        StockMove::Arrived {
                            info: info.clone(),
                            outgoing,
                            incoming: *incoming,
                        }
                    },
                    _ => state,
                }
            },
        }
    }
}

impl Event<Stock> for MoveEvent {
    type Output = Stock;

    fn apply_to(&self, state: Stock) -> Self::Output {
        match &state {
            Stock::Unmanaged { .. } => state,
            Stock::Managed { item: s_item, location: s_loc, 
                qty: s_qty, assigned: s_assigned } => {

                match self {
                    Self::Assigned { item, from, assigned } 
                    if s_item == item && s_loc == from => {

                        state.update_assigned(
                            s_assigned + assigned
                        )
                    },
                    Self::Shipped { item, from, outgoing }
                    if s_item == item && s_loc == from => {

                        state.update_qty(
                            s_qty.checked_sub(*outgoing).unwrap_or(0)
                        )
                    },
                    Self::AssignShipped { item, from, outgoing, assigned }
                    if s_item == item && s_loc == from => {

                        state.update(
                            s_qty.checked_sub(*outgoing).unwrap_or(0),
                            s_assigned.checked_sub(*assigned).unwrap_or(0),
                        )
                    },
                    Self::Arrived { item, to, incoming }
                    if s_item == item && s_loc == to => {

                        state.update_qty(
                            s_qty + incoming
                        )
                    },
                    _ => state,
                }
            },
        }
    }
}

impl<S, E> Restore<&E> for S
where
    Self: Clone,
    E: Event<Self, Output = Self>,
{
    fn restore(self, events: slice::Iter<&E>) -> Self {
        events.fold(self, |acc, ev| ev.apply_to(acc.clone()))
    }
}
