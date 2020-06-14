
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
pub enum StockCommand {
    Create(StockId),
    Ship(StockId, Quantity),
    Arrive(StockId, Quantity),
}

impl StockCommand {
    pub fn create(id: StockId) -> Self {
        Self::Create(id)
    }

    pub fn ship(id: StockId, qty: Quantity) -> Self {
        Self::Ship(id, qty)
    }

    pub fn arrive(id: StockId, qty: Quantity) -> Self {
        Self::Arrive(id, qty)
    }
}

#[derive(Debug, Clone)]
pub enum StockEvent {
    Created(StockId),
    Shipped(StockId, Quantity),
    Arrived(StockId, Quantity),
}

impl CommandHandle<Stock, StockEvent> for StockCommand {
    fn handle(&self, state: Stock) -> Option<StockEvent> {
        match self {
            StockCommand::Create(id) => {
                match state {
                    Stock::Nothing => {
                        Some(
                            StockEvent::Created(id.clone())
                        )
                    },
                    _ => None
                }
            },
            StockCommand::Ship(id, qty) => {
                match state {
                    Stock::InStock { id: sid, qty: sqty } 
                    if sid == id.clone() && sqty >= qty.clone() => {
                        Some(
                            StockEvent::Shipped(id.clone(), qty.clone())
                        )
                    },
                    _ => None
                }
            },
            StockCommand::Arrive(id, qty) => {
                match state {
                    Stock::OutOfStock { id: sid } | 
                    Stock::InStock { id: sid, .. } if sid == id.clone() => {
                        Some(
                            StockEvent::Arrived(id.clone(), qty.clone())
                        )
                    },
                    _ => None
                }
            },
        }
    }
}

impl EventApply<Stock> for StockEvent {
    fn apply(&self, state: Stock) -> Option<Stock> {
        match self {
            StockEvent::Created(id) => {
                match state {
                    Stock::Nothing => Some(
                        Stock::OutOfStock { id: id.clone() }
                    ),
                    _ => None
                }
            },
            StockEvent::Shipped(id, qty) => {
                match state {
                    Stock::InStock { id: sid, qty: sqty } 
                    if sid == id.clone() && sqty == qty.clone() => {
                        Some(
                            Stock::OutOfStock { id: id.clone() }
                        )
                    },
                    Stock::InStock { id: sid, qty: sqty } 
                    if sid == id.clone() && sqty > qty.clone() => {
                        Some(
                            Stock::InStock { id: id.clone(), qty: sqty - qty }
                        )
                    },
                    _ => None
                }
            },
            StockEvent::Arrived(id, qty) => {
                match state {
                    Stock::OutOfStock { id: sid } if sid == id.clone() => {
                        Some(
                            Stock::InStock { id: id.clone(), qty: qty.clone() }
                        )
                    },
                    Stock::InStock { id: sid, qty: sqty } if sid == id.clone() => {
                        Some(
                            Stock::InStock { id: id.clone(), qty: sqty + qty }
                        )
                    },
                    _ => None
                }
            },
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
