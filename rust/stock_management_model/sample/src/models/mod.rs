
use std::convert::{TryFrom, TryInto};
use std::slice;

pub type ItemCode = String;
pub type LocationCode = String;
pub type Quantity = u32;
pub type StockId = (ItemCode, LocationCode);
pub type StockMoveId = String;

pub trait Command<S, E> {
    fn handle(&self, state: S) -> Option<E>;
}

pub trait Event<S> {
    fn apply(&self, state: S) -> Option<S>;
}

#[allow(dead_code)]
#[derive(Debug, Clone)]
pub enum Stock {
    Managed { id: StockId, qty: Quantity, assigned: Quantity },
    Unmanaged { id: StockId },
}

#[allow(dead_code)]
impl Stock {
    pub fn managed_new(item: ItemCode, location: LocationCode) -> Self {
        Self::Managed { id: (item, location), qty: 0, assigned: 0 }
    }

    pub fn unmanaged_new(item: ItemCode, location: LocationCode) -> Self {
        Self::Unmanaged { id: (item, location) }
    }

    pub fn eq_id(&self, item: &ItemCode, location: &LocationCode) -> bool {
        match self {
            Self::Managed { id, .. } | Self::Unmanaged { id } => 
                id.clone() == (item.clone(), location.clone())
        }
    }

    pub fn is_sufficient(&self, v: Quantity) -> bool {
        match self {
            Self::Managed { qty, assigned, .. } =>
                v + assigned <= qty.clone(),
            Self::Unmanaged { .. } => true, 
        }
    }

    fn update(&self, qty: Quantity, assigned: Quantity) -> Self {
        match self {
            Self::Managed { id, .. } => {
                Self::Managed {
                    id: id.clone(),
                    qty: qty,
                    assigned: assigned,
                }
            },
            Self::Unmanaged { .. } => self.clone(),
        }
    }

    pub(super) fn update_qty(&self, qty: Quantity) -> Self {
        match self {
            Self::Managed { assigned, .. } => self.update(qty, assigned.clone()),
            Self::Unmanaged { .. } => self.clone(),
        }
    }

    fn update_assigned(&self, assigned: Quantity) -> Self {
        match self {
            Self::Managed { qty, .. } => self.update(qty.clone(), assigned),
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

#[derive(Debug, Clone, PartialEq)]
pub enum StockMove {
    Nothing,
    Draft { id: StockMoveId, info: StockMoveInfo },
    Assigned { id: StockMoveId, info: StockMoveInfo, 
        assigned: Quantity },
    Shipped { id: StockMoveId, info: StockMoveInfo, 
        outgoing: Quantity },
    Arrived { id: StockMoveId, info: StockMoveInfo, 
        incoming: Quantity },
    Cancelled { id: StockMoveId, info: StockMoveInfo },
    AssignFailed { id: StockMoveId, info: StockMoveInfo },
    ShipmentFailed { id: StockMoveId, info: StockMoveInfo },
}

impl StockMove {
    fn info(&self) -> Option<StockMoveInfo> {
        match self {
            Self::Draft { info, .. } |
            Self::Assigned { info, .. } |
            Self::Shipped { info, .. } |
            Self::Arrived { info, .. } |
            Self::Cancelled { info, .. } |
            Self::AssignFailed { info, .. } |
            Self::ShipmentFailed { info, .. } => Some(info.clone()),
            _ => None,
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct StartCommand {
    move_id: StockMoveId, 
    item: ItemCode,
    qty: Quantity, 
    from: LocationCode, 
    to: LocationCode,
}

#[derive(Debug, Clone, PartialEq)]
pub struct AssignCommand {
    move_id: StockMoveId, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct CancelCommand {
    move_id: StockMoveId, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct ShipmentReport {
    move_id: StockMoveId, 
    outgoing: Quantity, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct ShipmentFailureReport {
    move_id: StockMoveId, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct ArrivalReport {
    move_id: StockMoveId, 
    incoming: Quantity, 
}

#[allow(dead_code)]
#[derive(Debug, Clone, PartialEq)]
pub enum StockMoveAction {
    Start(StartCommand),
    Assign(AssignCommand),
    Cancel(CancelCommand),
    Shipment(ShipmentReport),
    ShipmentFailure(ShipmentFailureReport),
    Arrival(ArrivalReport),
}

#[allow(dead_code)]
impl StockMoveAction {
    pub fn start(move_id: StockMoveId, item: ItemCode, qty: Quantity, from: LocationCode, to: LocationCode) -> Option<Self> {
        if qty > 0 {
            Some(
                Self::Start(StartCommand { move_id, item, qty, from, to })
            )
        } else {
            None
        }
    }

    pub fn assign(move_id: StockMoveId) -> Option<Self> {
        Some(Self::Assign(AssignCommand { move_id }))
    }

    pub fn cancel(move_id: StockMoveId) -> Option<Self> {
        Some(Self::Cancel(CancelCommand { move_id }))
    }

    pub fn shipment(move_id: StockMoveId, qty: Quantity) -> Option<Self> {
        if qty > 0 {
            Some(Self::Shipment(ShipmentReport { move_id, outgoing: qty }))
        } else {
            None
        }
    }

    pub fn shipment_failure(move_id: StockMoveId) -> Option<Self> {
        Some(Self::ShipmentFailure(ShipmentFailureReport { move_id }))
    }

    pub fn arrival(move_id: StockMoveId, qty: Quantity) -> Option<Self> {
        Some(Self::Arrival(ArrivalReport { move_id, incoming: qty }))
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct StartedEvent {
    move_id: StockMoveId, 
    item: ItemCode, 
    qty: Quantity, 
    from: LocationCode, 
    to: LocationCode,
}

#[derive(Debug, Clone, PartialEq)]
pub struct AssignedEvent { 
    move_id: StockMoveId, 
    item: ItemCode, 
    from: LocationCode,
    assigned: Quantity, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct ShippedEvent { 
    move_id: StockMoveId, 
    item: ItemCode, 
    from: LocationCode,
    outgoing: Quantity, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct AssignShippedEvent { 
    move_id: StockMoveId, 
    item: ItemCode, 
    from: LocationCode, 
    outgoing: Quantity, 
    assigned: Quantity, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct ArrivedEvent {
    move_id: StockMoveId, 
    item: ItemCode, 
    to: LocationCode,
    incoming: Quantity, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct CancelledEvent {
    move_id: StockMoveId, 
}

#[derive(Debug, Clone, PartialEq)]
pub struct AssignFailedEvent { 
    move_id: StockMoveId, 
    item: ItemCode, 
    qty: Quantity, 
    from: LocationCode,
}

#[derive(Debug, Clone, PartialEq)]
pub struct ShipmentFailedEvent { 
    move_id: StockMoveId, 
    item: ItemCode, 
    qty: Quantity, 
    from: LocationCode,
}

#[derive(Debug, Clone, PartialEq)]
pub struct AssignShipmentFailedEvent { 
    move_id: StockMoveId, 
    item: ItemCode, 
    qty: Quantity, 
    from: LocationCode, 
    assigned: Quantity, 
}

#[derive(Debug, Clone, PartialEq)]
pub enum StockMoveEvent {
    Started(StartedEvent),
    Assigned(AssignedEvent),
    Shipped(ShippedEvent),
    AssignShipped(AssignShippedEvent),
    Arrived(ArrivedEvent),
    Cancelled(CancelledEvent),
    AssignFailed(AssignFailedEvent),
    ShipmentFailed(ShipmentFailedEvent),
    AssignShipmentFailed(AssignShipmentFailedEvent),
}

impl TryFrom<(&StartCommand, &StockMove)> for StockMoveEvent {
    type Error = ();

    fn try_from(v: (&StartCommand, &StockMove)) -> Result<Self, Self::Error> {
        let (cmd, state) = v;

        if *state == StockMove::Nothing {
            Ok(
                StockMoveEvent::Started(
                    StartedEvent {
                        move_id: cmd.move_id.clone(), 
                        item: cmd.item.clone(), 
                        qty: cmd.qty.clone(), 
                        from: cmd.from.clone(), 
                        to: cmd.to.clone(),
                    }
                )
            )
        } else {
            Err(())
        }
    }
}

impl TryFrom<(&CancelCommand, &StockMove)> for StockMoveEvent {
    type Error = ();

    fn try_from(v: (&CancelCommand, &StockMove)) -> Result<Self, Self::Error> {
        let (cmd, state) = v;

        match state {
            StockMove::Draft { id, .. } if cmd.move_id == id.clone() => {
                Ok(
                    StockMoveEvent::Cancelled(
                        CancelledEvent { move_id: cmd.move_id.clone() }
                    )
                )
            },
            _ => Err(()),
        }
    }
}

impl TryFrom<(&AssignCommand, &StockMove, &Stock)> for StockMoveEvent {
    type Error = ();

    fn try_from(v: (&AssignCommand, &StockMove, &Stock)) -> Result<Self, Self::Error> {
        let (cmd, state, stock) = v;

        match state {
            StockMove::Draft { id, info, .. } if cmd.move_id == id.clone() => {
                Some(stock)
                    .filter(|s| s.eq_id(&info.item, &info.from))
                    .map(|s|
                        if s.is_sufficient(info.qty) {
                            StockMoveEvent::Assigned(
                                AssignedEvent {
                                    move_id: cmd.move_id.clone(), 
                                    item: info.item.clone(),
                                    from: info.from.clone(), 
                                    assigned: info.qty.clone(),
                                }
                            )
                        } else {
                            StockMoveEvent::AssignFailed(
                                AssignFailedEvent {
                                    move_id: cmd.move_id.clone(), 
                                    item: info.item.clone(),
                                    qty: info.qty.clone(), 
                                    from: info.from.clone(), 
                                }
                            )
    
                        }
                    ).ok_or(())
            },
            _ => Err(()),
        }
    }
}

impl TryFrom<(&ShipmentReport, &StockMove)> for StockMoveEvent {
    type Error = ();

    fn try_from(v: (&ShipmentReport, &StockMove)) -> Result<Self, Self::Error> {
        let (cmd, state) = v;

        match state {
            StockMove::Draft { id, info, .. } if cmd.move_id == id.clone() => {
                Ok(
                    StockMoveEvent::Shipped(
                        ShippedEvent {
                            move_id: cmd.move_id.clone(), 
                            item: info.item.clone(),
                            from: info.from.clone(),
                            outgoing: cmd.outgoing.clone(),
                        }
                    )
                )
            },
            StockMove::Assigned { id, info, assigned } if cmd.move_id == id.clone() => {
                Ok(
                    StockMoveEvent::AssignShipped(
                        AssignShippedEvent {
                            move_id: cmd.move_id.clone(), 
                            item: info.item.clone(),
                            from: info.from.clone(),
                            outgoing: cmd.outgoing.clone(),
                            assigned: assigned.clone(),
                        }
                    )
                )
            },
            _ => Err(()),
        }
    }
}

impl TryFrom<(&ShipmentFailureReport, &StockMove)> for StockMoveEvent {
    type Error = ();

    fn try_from(v: (&ShipmentFailureReport, &StockMove)) -> Result<Self, Self::Error> {
        let (cmd, state) = v;

        match state {
            StockMove::Draft { id, info, .. } if cmd.move_id == id.clone() => {
                Ok(
                    StockMoveEvent::ShipmentFailed(
                        ShipmentFailedEvent {
                            move_id: cmd.move_id.clone(), 
                            item: info.item.clone(),
                            qty: info.qty.clone(), 
                            from: info.from.clone(),
                        }
                    )
                )
            },
            StockMove::Assigned { id, info, assigned } if cmd.move_id == id.clone() => {
                Ok(
                    StockMoveEvent::AssignShipmentFailed(
                        AssignShipmentFailedEvent {
                            move_id: cmd.move_id.clone(), 
                            item: info.item.clone(),
                            qty: info.qty.clone(), 
                            from: info.from.clone(),
                            assigned: assigned.clone(),
                        }
                    )
                )
            },
            _ => Err(()),
        }
    }
}

impl TryFrom<(&ArrivalReport, &StockMove)> for StockMoveEvent {
    type Error = ();

    fn try_from(v: (&ArrivalReport, &StockMove)) -> Result<Self, Self::Error> {
        let (cmd, state) = v;

        match state {
            StockMove::Draft { id, info, .. } | 
            StockMove::Shipped { id, info, .. } 
            if cmd.move_id == id.clone() => {
                Ok(
                    StockMoveEvent::Arrived(
                        ArrivedEvent {
                            move_id: cmd.move_id.clone(), 
                            item: info.item.clone(),
                            to: info.to.clone(),
                            incoming: cmd.incoming.clone(),
                        }
                    )
                )
            },
            _ => Err(()),
        }
    }
}

impl Event<StockMove> for StartedEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Nothing => 
                Some(
                    StockMove::Draft {
                        id: self.move_id.clone(), 
                        info: StockMoveInfo {
                            item: self.item.clone(), 
                            qty: self.qty.clone(), 
                            from: self.from.clone(), 
                            to: self.to.clone(),
                        },
                    }
                ),
        _ => None,
        }
    }
}

impl Event<StockMove> for AssignedEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Draft { id, info } 
            if id == self.move_id && info.item == self.item.clone() => 
                Some(
                    StockMove::Assigned {
                        id,
                        info,
                        assigned: self.assigned,
                    }
                ),
        _ => None,
        }
    }
}

impl Event<StockMove> for ShippedEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Draft { id, info } 
            if id == self.move_id && info.item == self.item.clone() => 
                Some(
                    StockMove::Shipped {
                        id,
                        info,
                        outgoing: self.outgoing,
                    }
                ),
        _ => None,
        }
    }
}

impl Event<StockMove> for AssignShippedEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Assigned { id, info, assigned } 
            if id == self.move_id && 
                info.item == self.item.clone() &&
                assigned == self.assigned => 

                Some(
                    StockMove::Shipped {
                        id,
                        info,
                        outgoing: self.outgoing,
                    }
                ),
        _ => None,
        }
    }
}

impl Event<StockMove> for ArrivedEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Draft { id, info } |
            StockMove::Shipped { id, info, .. } 
            if id == self.move_id && info.item == self.item.clone() => 
                Some(
                    StockMove::Arrived {
                        id,
                        info,
                        incoming: self.incoming,
                    }
                ),
        _ => None,
        }
    }
}

impl Event<StockMove> for CancelledEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Draft { id, info } if id == self.move_id => 
                Some(
                    StockMove::Cancelled { id, info }
                ),
        _ => None,
        }
    }
}

impl Event<StockMove> for AssignFailedEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Draft { id, info } if id == self.move_id => 
                Some(
                    StockMove::AssignFailed { id, info }
                ),
        _ => None,
        }
    }
}

impl Event<StockMove> for ShipmentFailedEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Draft { id, info } if id == self.move_id => 
                Some(
                    StockMove::ShipmentFailed { id, info }
                ),
        _ => None,
        }
    }
}

impl Event<StockMove> for AssignShipmentFailedEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match state {
            StockMove::Assigned { id, info, assigned } 
            if id == self.move_id &&
                info.item == self.item.clone() &&
                assigned == self.assigned.clone() => 

                Some(
                    StockMove::ShipmentFailed { id, info }
                ),
        _ => None,
        }
    }
}


impl Event<StockMove> for StockMoveEvent {
    fn apply(&self, state: StockMove) -> Option<StockMove> {
        match self {
            StockMoveEvent::Started(d) => d.apply(state),
            StockMoveEvent::Assigned(d) => d.apply(state),
            StockMoveEvent::Shipped(d) => d.apply(state),
            StockMoveEvent::AssignShipped(d) => d.apply(state),
            StockMoveEvent::Arrived(d) => d.apply(state),
            StockMoveEvent::Cancelled(d) => d.apply(state),
            StockMoveEvent::AssignFailed(d) => d.apply(state),
            StockMoveEvent::ShipmentFailed(d) => d.apply(state),
            StockMoveEvent::AssignShipmentFailed(d) => d.apply(state),
        }
    }
}

pub trait Action<C, E> {
    fn action(&self, cmd: C) -> Option<E>;
}

pub trait Restore<E> {
    fn restore(self, events: slice::Iter<E>) -> Self;
}

impl<T> Action<&StockMoveAction, StockMoveEvent> for (StockMove, T)
where
    T: Fn(ItemCode, LocationCode) -> Option<Stock>,
{
    fn action(&self, cmd: &StockMoveAction) -> Option<StockMoveEvent> {
        let (state, find) = self;

        match cmd {
            StockMoveAction::Start(d) => (d, state).try_into().ok(),
            StockMoveAction::Assign(d) => {
                state.info()
                    .and_then(|info| find(info.item, info.from))
                    .and_then(|stock| (d, state, &stock).try_into().ok())
            },
            StockMoveAction::Cancel(d) => (d, state).try_into().ok(),
            StockMoveAction::Shipment(d) => (d, state).try_into().ok(),
            StockMoveAction::ShipmentFailure(d) => (d, state).try_into().ok(),
            StockMoveAction::Arrival(d) => (d, state).try_into().ok(),
        }
    }
}

impl Restore<&StockMoveEvent> for StockMove {
    fn restore(self, events: slice::Iter<&StockMoveEvent>) -> Self {
        events.fold(self, |acc, ev| ev.apply(acc.clone()).unwrap_or(acc))
    }
}

impl Event<Stock> for AssignedEvent {
    fn apply(&self, state: Stock) -> Option<Stock> {
        if state.eq_id(&self.item, &self.from) {
            match state {
                Stock::Managed { assigned, .. } => {
                    assigned.checked_add(self.assigned)
                        .or_else(|| Some(Quantity::MAX))
                        .map(|a| state.update_assigned(a))
                },
                Stock::Unmanaged { .. } => None,
            }
        } else {
            None
        }
    }
}

impl Event<Stock> for ShippedEvent {
    fn apply(&self, state: Stock) -> Option<Stock> {
        if state.eq_id(&self.item, &self.from) {
            match state {
                Stock::Managed { qty, .. } => {
                    qty.checked_sub(self.outgoing)
                        .or_else(|| Some(0))
                        .map(|q| state.update_qty(q))
                },
                Stock::Unmanaged { .. } => None,
            }
        } else {
            None
        }
    }
}

impl Event<Stock> for AssignShippedEvent {
    fn apply(&self, state: Stock) -> Option<Stock> {
        if state.eq_id(&self.item, &self.from) {
            match state {
                Stock::Managed { qty, assigned, .. } => {
                    let q = qty.checked_sub(self.outgoing).unwrap_or(0);
                    let a = assigned.checked_sub(self.assigned).unwrap_or(0);

                    Some(state.update(q, a))
                },
                Stock::Unmanaged { .. } => None,
            }
        } else {
            None
        }
    }
}

impl Event<Stock> for ArrivedEvent {
    fn apply(&self, state: Stock) -> Option<Stock> {
        if state.eq_id(&self.item, &self.to) {
            match state {
                Stock::Managed { qty, .. } => {
                    qty.checked_add(self.incoming)
                        .or_else(|| Some(Quantity::MAX))
                        .map(|q| state.update_qty(q))
                },
                Stock::Unmanaged { .. } => None,
            }
        } else {
            None
        }
    }
}

impl Event<Stock> for AssignShipmentFailedEvent {
    fn apply(&self, state: Stock) -> Option<Stock> {
        if state.eq_id(&self.item, &self.from) {
            match state {
                Stock::Managed { assigned, .. } => {
                    assigned.checked_sub(self.assigned)
                        .or_else(|| Some(0))
                        .map(|a| state.update_assigned(a))
                },
                Stock::Unmanaged { .. } => None,
            }
        } else {
            None
        }
    }
}

impl Event<Stock> for StockMoveEvent {
    fn apply(&self, state: Stock) -> Option<Stock> {
        match self {
            StockMoveEvent::Assigned(d) => d.apply(state),
            StockMoveEvent::Shipped(d) => d.apply(state),
            StockMoveEvent::AssignShipped(d) => d.apply(state),
            StockMoveEvent::Arrived(d) => d.apply(state),
            StockMoveEvent::AssignShipmentFailed(d) => d.apply(state),
            _ => None,
        }
    }
}

impl Restore<&StockMoveEvent> for Stock {
    fn restore(self, events: slice::Iter<&StockMoveEvent>) -> Self {
        events.fold(self, |acc, ev| ev.apply(acc.clone()).unwrap_or(acc))
    }
}
