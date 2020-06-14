
pub type StockId = String;
pub type Quantity = u32;

#[derive(Debug, Clone)]
pub enum Stock {
    Nothing,
    OutOfStock { id: StockId },
    InStock { id: StockId, qty: Quantity },
}

pub trait CommandHandle<S, E> {
    fn handle(&self, state: S) -> Option<E>;
}

pub trait EventApply<S> {
    fn apply(&self, state: S) -> Option<S>;
}

#[derive(Debug, Clone)]
pub struct CreateStock(pub StockId);

#[derive(Debug, Clone)]
pub struct ShipStock(pub StockId, pub Quantity);

#[derive(Debug, Clone)]
pub struct ArriveStock(pub StockId, pub Quantity);

#[derive(Debug, Clone)]
pub enum StockCommand {
    Create(CreateStock),
    Ship(ShipStock),
    Arrive(ArriveStock),
}

impl StockCommand {
    pub fn create(id: StockId) -> Self {
        Self::Create(CreateStock(id))
    }

    pub fn ship(id: StockId, qty: Quantity) -> Self {
        Self::Ship(ShipStock(id, qty))
    }

    pub fn arrive(id: StockId, qty: Quantity) -> Self {
        Self::Arrive(ArriveStock(id, qty))
    }
}

#[derive(Debug, Clone)]
pub struct StockCreated(StockId);

#[derive(Debug, Clone)]
pub struct StockShipped(StockId, Quantity);

#[derive(Debug, Clone)]
pub struct StockArrived(StockId, Quantity);

#[derive(Debug, Clone)]
pub enum StockEvent {
    Created(StockCreated),
    Shipped(StockShipped),
    Arrived(StockArrived),
}

impl CommandHandle<Stock, StockEvent> for CreateStock {
    fn handle(&self, state: Stock) -> Option<StockEvent> {
        match state {
            Stock::Nothing => {
                Some(
                    StockEvent::Created(
                        StockCreated(self.0.clone())
                    )
                )
            },
            _ => None
        }
    }
}

impl CommandHandle<Stock, StockEvent> for ShipStock {
    fn handle(&self, state: Stock) -> Option<StockEvent> {
        match state {
            Stock::InStock { id, qty } if id == self.0 && qty >= self.1 => {
                Some(
                    StockEvent::Shipped(
                        StockShipped(self.0.clone(), self.1)
                    )
                )
            },
            _ => None
        }
    }
}

impl CommandHandle<Stock, StockEvent> for ArriveStock {
    fn handle(&self, state: Stock) -> Option<StockEvent> {
        match state {
            Stock::OutOfStock { id } | 
            Stock::InStock { id, .. } if id == self.0 => {
                Some(
                    StockEvent::Arrived(
                        StockArrived(self.0.clone(), self.1)
                    )
                )
            },
            _ => None
        }
    }
}

impl CommandHandle<Stock, StockEvent> for StockCommand {
    fn handle(&self, state: Stock) -> Option<StockEvent> {
        match self {
            StockCommand::Create(d) => d.handle(state),
            StockCommand::Ship(d) => d.handle(state),
            StockCommand::Arrive(d) => d.handle(state),
        }
    }
}

impl EventApply<Stock> for StockCreated {
    fn apply(&self, state: Stock) -> Option<Stock> {
        match state {
            Stock::Nothing => Some(
                Stock::OutOfStock { id: self.0.clone() }
            ),
            _ => None
        }
    }
}

impl EventApply<Stock> for StockShipped {
    fn apply(&self, state: Stock) -> Option<Stock> {
        match state {
            Stock::InStock { id, qty } if id == self.0 && qty == self.1 => {
                Some(
                    Stock::OutOfStock { id: self.0.clone() }
                )
            },
            Stock::InStock { id, qty } if id == self.0 && qty > self.1 => {
                Some(
                    Stock::InStock { id: self.0.clone(), qty: qty - self.1 }
                )
            },
            _ => None
        }
    }
}

impl EventApply<Stock> for StockArrived {
    fn apply(&self, state: Stock) -> Option<Stock> {
        match state {
            Stock::OutOfStock { id } if id == self.0 => {
                Some(
                    Stock::InStock { id: self.0.clone(), qty: self.1 }
                )
            },
            Stock::InStock { id, qty } if id == self.0 => {
                Some(
                    Stock::InStock { id: self.0.clone(), qty: qty + self.1 }
                )
            },
            _ => None
        }
    }
}

impl EventApply<Stock> for StockEvent {
    fn apply(&self, state: Stock) -> Option<Stock> {
        match self {
            StockEvent::Created(d) => d.apply(state),
            StockEvent::Shipped(d) => d.apply(state),
            StockEvent::Arrived(d) => d.apply(state),
        }
    }
}

impl Stock {
    pub fn action(self, cmd: &StockCommand) -> Option<(Stock, StockEvent)> {
        cmd.handle(self.clone())
            .and_then(|ev|
                ev.apply(self).map(|s| (s, ev))
            )
    }

    pub fn restore(self, events: Vec<&StockEvent>) -> Stock {
        events.iter().fold(self, |acc, x| x.apply(acc.clone()).unwrap_or(acc))
    }
}
