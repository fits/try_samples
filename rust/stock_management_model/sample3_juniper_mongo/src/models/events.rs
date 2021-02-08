use serde::{Deserialize, Serialize};

#[allow(dead_code)]
#[derive(Debug, Clone, PartialEq, Deserialize, Serialize)]
#[serde(tag = "type")]
pub enum StockMoveEvent<Item, Location, Quantity> {
    Started {
        item: Item,
        qty: Quantity,
        from: Location,
        to: Location,
    },
    Completed,
    Cancelled,
    Assigned {
        item: Item,
        from: Location,
        assigned: Quantity,
    },
    Shipped {
        item: Item,
        from: Location,
        outgoing: Quantity,
    },
    AssignShipped {
        item: Item,
        from: Location,
        outgoing: Quantity,
        assigned: Quantity,
    },
    Arrived {
        item: Item,
        to: Location,
        incoming: Quantity,
    },
}
