
use std::convert::{TryFrom, TryInto};

pub type StockId = String;
pub type Quantity = u32;

#[derive(Debug, Clone)]
pub enum Stock {
    Nothing,
    OutOfStock { id: StockId },
    InStock { id: StockId, qty: Quantity },
}

pub trait CommandHandle<S, E>: std::fmt::Debug {
    fn handle(&self, state: S) -> Option<E>;
}

pub trait EventApply<S>: std::fmt::Debug {
    fn apply(&self, state: S) -> Option<S>;
}

pub type StockEvent = Box<dyn EventApply<Stock>>;

#[derive(Debug, Clone)]
pub struct CreateStock(pub StockId);

#[derive(Debug, Clone)]
pub struct ShipStock(pub StockId, pub Quantity);

#[derive(Debug, Clone)]
pub struct ArriveStock(pub StockId, pub Quantity);

impl TryFrom<&CreateStock> for StockEvent {
    type Error = ();

    fn try_from(cmd: &CreateStock) -> Result<Self, Self::Error> {
        let event = StockCreated(cmd.0.clone());
        Ok(Box::new(event))
    }
}

impl TryFrom<&ShipStock> for StockEvent {
    type Error = ();

    fn try_from(cmd: &ShipStock) -> Result<Self, Self::Error> {
        if cmd.1 > 0 {
            let event = StockShipped(cmd.0.clone(), cmd.1);
            Ok(Box::new(event))
        } else {
            Err(())
        }
    }
}

impl TryFrom<&ArriveStock> for StockEvent {
    type Error = ();

    fn try_from(cmd: &ArriveStock) -> Result<Self, Self::Error> {
        if cmd.1 > 0 {
            let event = StockArrived(cmd.0.clone(), cmd.1);
            Ok(Box::new(event))
        } else {
            Err(())
        }
    }
}

impl CommandHandle<Stock, StockEvent> for CreateStock {
    fn handle(&self, state: Stock) -> Option<StockEvent> {
        match state {
            Stock::Nothing => {
                self.try_into().ok()
            },
            _ => None
        }
    }
}

impl CommandHandle<Stock, StockEvent> for ShipStock {
    fn handle(&self, state: Stock) -> Option<StockEvent> {
        match state {
            Stock::OutOfStock { id } | 
            Stock::InStock { id, .. } if id == self.0 => {
                self.try_into().ok()
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
                self.try_into().ok()
            },
            _ => None
        }
    }
}

#[derive(Debug, Clone)]
pub struct StockCreated(StockId);

#[derive(Debug, Clone)]
pub struct StockShipped(StockId, Quantity);

#[derive(Debug, Clone)]
pub struct StockArrived(StockId, Quantity);

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

impl Stock {
    pub fn handle<C>(self, cmd: &C) -> Option<(Stock, StockEvent)> 
    where
        C: CommandHandle<Stock, StockEvent>,
    {
        cmd.handle(self.clone())
            .and_then(|ev|
                ev.apply(self).map(|s| (s, ev))
            )
    }

    pub fn restore(self, events: Vec<&StockEvent>) -> Stock {
        events.iter().fold(self, |acc, x| x.apply(acc.clone()).unwrap_or(acc))
    }
}
