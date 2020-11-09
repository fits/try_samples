
export type ItemCode = string
export type LocationCode = string
export type Quantity = number

export interface StockMoveEventStarted {
    tag: 'stock-move-event.started'
    item: ItemCode
    qty: Quantity
    from: LocationCode
    to: LocationCode
}

export interface StockMoveEventCompleted {
    tag: 'stock-move-event.completed'
}

export interface StockMoveEventCancelled {
    tag: 'stock-move-event.cancelled'
}

export interface StockMoveEventAssigned {
    tag: 'stock-move-event.assigned'
    item: ItemCode
    from: LocationCode
    assigned: Quantity
}

export interface StockMoveEventShipped {
    tag: 'stock-move-event.shipped'
    item: ItemCode
    from: LocationCode
    outgoing: Quantity
}

export interface StockMoveEventAssignShipped {
    tag: 'stock-move-event.assign-shipped'
    item: ItemCode
    from: LocationCode
    outgoing: Quantity
    assigned: Quantity
}

export interface StockMoveEventArrived {
    tag: 'stock-move-event.arrived'
    item: ItemCode
    to: LocationCode
    incoming: Quantity
}

export type StockMoveEvent = 
    StockMoveEventStarted | StockMoveEventCompleted | StockMoveEventCancelled | 
    StockMoveEventAssigned | StockMoveEventShipped | StockMoveEventAssignShipped | 
    StockMoveEventArrived
