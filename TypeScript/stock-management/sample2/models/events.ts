
export interface StockMoveEventStarted<Item, Location, Quantity> {
    tag: 'stock-move-event.started'
    item: Item
    qty: Quantity
    from: Location
    to: Location
}

export interface StockMoveEventCompleted {
    tag: 'stock-move-event.completed'
}

export interface StockMoveEventCancelled {
    tag: 'stock-move-event.cancelled'
}

export interface StockMoveEventAssigned<Item, Location, Quantity> {
    tag: 'stock-move-event.assigned'
    item: Item
    from: Location
    assigned: Quantity
}

export interface StockMoveEventShipped<Item, Location, Quantity> {
    tag: 'stock-move-event.shipped'
    item: Item
    from: Location
    outgoing: Quantity
}

export interface StockMoveEventAssignShipped<Item, Location, Quantity> {
    tag: 'stock-move-event.assign-shipped'
    item: Item
    from: Location
    outgoing: Quantity
    assigned: Quantity
}

export interface StockMoveEventArrived<Item, Location, Quantity> {
    tag: 'stock-move-event.arrived'
    item: Item
    to: Location
    incoming: Quantity
}

export type StockMoveEvent<Item, Location, Quantity> = 
    StockMoveEventStarted<Item, Location, Quantity> | 
    StockMoveEventCompleted | 
    StockMoveEventCancelled | 
    StockMoveEventAssigned<Item, Location, Quantity> | 
    StockMoveEventShipped<Item, Location, Quantity> | 
    StockMoveEventAssignShipped<Item, Location, Quantity> | 
    StockMoveEventArrived<Item, Location, Quantity>
