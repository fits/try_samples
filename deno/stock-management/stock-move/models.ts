
export type ItemCode = string
export type LocationCode = string
export type Quantity = number

export interface ManagedStock {
    tag: 'managed'
    readonly item: ItemCode
    readonly location: LocationCode
    readonly qty: Quantity
    readonly assigned: Quantity
}

export interface UnmanagedStock {
    tag: 'unmanaged'
    readonly item: ItemCode
    readonly location: LocationCode
}

export type Stock = ManagedStock | UnmanagedStock

export class StockFunc {
    static newManaged(item: ItemCode, location: LocationCode): ManagedStock {
        return {
            tag: 'managed',
            item,
            location,
            qty: 0,
            assigned: 0
        }
    }

    static newUnmanaged(item: ItemCode, location: LocationCode): UnmanagedStock {
        return {
            tag: 'unmanaged',
            item,
            location
        }
    }

    static isSufficient(stock: Stock, q: Quantity) {
        switch (stock.tag) {
            case 'managed':
                return q >= 0 && q <= stock.qty - stock.assigned
            case 'unmanaged':
                return q >= 0
        }
    }

    static isTarget(stock: Stock, item: ItemCode, location: LocationCode) {
        return stock.item == item && stock.location == location
    }

    static update(stock: Stock, qty: Quantity, assigned: Quantity): Stock {
        if (stock.tag == 'managed') {
            qty = Math.max(qty, 0)
            assigned = Math.max(assigned, 0)

            return { ...stock, qty, assigned }
        }

        return stock
    }
}

interface StockMoveInfo {
    readonly item: ItemCode
    readonly qty: Quantity
    readonly from: LocationCode
    readonly to: LocationCode
}

interface NothingStockMove { 
    tag: 'nothing-move'
}

interface DraftStockMove {
    tag: 'draft-move'
    readonly info: StockMoveInfo
}

interface CancelledStockMove {
    tag: 'cancelled-move'
    readonly info: StockMoveInfo
}

interface AssignedStockMove {
    tag: 'assigned-move'
    readonly info: StockMoveInfo
    readonly assigned: Quantity
}

interface AssignFailedStockMove {
    tag: 'assign-failed-move'
    readonly info: StockMoveInfo
}

interface ShippedStockMove {
    tag: 'shipped-move'
    readonly info: StockMoveInfo
    readonly outgoing: Quantity
}

interface ShipmentFailedStockMove {
    tag: 'shipment-failed-move'
    readonly info: StockMoveInfo
}

interface ArrivedStockMove {
    tag: 'arrived-move'
    readonly info: StockMoveInfo
    readonly incoming: Quantity
}

type StockMove = 
    NothingStockMove | DraftStockMove | CancelledStockMove |
    AssignedStockMove | ShippedStockMove | ArrivedStockMove | 
    AssignFailedStockMove | ShipmentFailedStockMove

interface StartedEvent {
    tag: 'started'
    readonly item: ItemCode
    readonly qty: Quantity
    readonly from: LocationCode
    readonly to: LocationCode
}

interface CancelledEvent {
    tag: 'cancelled'
}

interface AssignedEvent {
    tag: 'assigned'
    readonly item: ItemCode
    readonly from: LocationCode
    readonly assigned: Quantity
}

interface AssignFailedEvent {
    tag: 'assign-failed'
    readonly item: ItemCode
    readonly qty: Quantity
    readonly from: LocationCode
}

interface ShippedEvent {
    tag: 'shipped'
    readonly item: ItemCode
    readonly from: LocationCode
    readonly outgoing: Quantity
}

interface ShipmentFailedEvent {
    tag: 'shipment-failed'
    readonly item: ItemCode
    readonly qty: Quantity
    readonly from: LocationCode
}

interface AssignShippedEvent {
    tag: 'assign-shipped'
    readonly item: ItemCode
    readonly from: LocationCode
    readonly outgoing: Quantity
    readonly assigned: Quantity
}

interface AssignShipmentFailedEvent {
    tag: 'assign-shipment-failed'
    readonly item: ItemCode
    readonly qty: Quantity
    readonly from: LocationCode
    readonly assigned: Quantity
}

interface ArrivedEvent {
    tag: 'arrived'
    readonly item: ItemCode
    readonly to: LocationCode
    readonly incoming: Quantity
}

export type StockMoveEvent = 
    StartedEvent | CancelledEvent | 
    AssignedEvent | ShippedEvent | AssignShippedEvent | ArrivedEvent |
    AssignFailedEvent | AssignShipmentFailedEvent | ShipmentFailedEvent

type FindStock = (item: ItemCode, location: LocationCode) => Stock | undefined

type MoveEventResult = StockMoveEvent | null

export class StockMoveAction {
    static start(state: StockMove, item: ItemCode, qty: Quantity, 
        from: LocationCode, to: LocationCode): MoveEventResult {

        return (qty > 0 && from != to && state.tag == 'nothing-move') ?
            { tag: 'started', item, qty, from, to } : null
    }

    static cancel(state: StockMove): MoveEventResult {
        return (state.tag == 'draft-move') ? { tag: 'cancelled' } : null
    }

    static assign(state: StockMove, finder: FindStock): MoveEventResult {
        if (state.tag == 'draft-move') {
            const { item, qty, from } = state.info
            const stock = finder(item, from)

            if (stock && StockFunc.isSufficient(stock, qty)) {
                return { tag: 'assigned', item, from, assigned: qty }
            }

            return { tag: 'assign-failed', item, qty, from }
        }

        return null
    }

    static ship(state: StockMove, outgoing: Quantity): MoveEventResult {
        switch (state.tag) {
            case 'draft-move': {
                const { item, qty, from } = state.info

                if (outgoing > 0) {
                    return { tag: 'shipped', item, from, outgoing }
                }

                return { tag: 'shipment-failed', item, qty, from }
            }
            case 'assigned-move': {
                const { info: { item, qty, from }, assigned } = state

                if (outgoing > 0) {
                    return { tag: 'assign-shipped', item, from, outgoing, assigned }
                }

                return { tag: 'assign-shipment-failed', item, qty, from, assigned }
            }
        }

        return null
    }

    static arrive(state: StockMove, incoming: Quantity): MoveEventResult {
        if (incoming >= 0) {
            switch (state.tag) {
                case 'draft-move': {
                    const { item, to } = state.info
                    return { tag: 'arrived', item, to, incoming }
                }
                case 'shipped-move': {
                    const { item, to } = state.info
                    return { tag: 'arrived', item, to, incoming }
                }
            }
        }

        return null
    }
}

export class StockMoveFunc {
    static initial(): NothingStockMove {
        return { tag: 'nothing-move' }
    }
}

export class StockMoveRestore {
    static restore(state: StockMove, events: StockMoveEvent[]): StockMove {
        return events.reduce(StockMoveRestore.apply, state)
    }

    private static apply(state: StockMove, event: StockMoveEvent): StockMove {
        switch (state.tag) {
            case 'nothing-move':
                switch (event.tag) {
                    case 'started':
                        return StockMoveRestore.applyStarted(state, event)
                }
                break
            case 'draft-move':
                switch (event.tag) {
                    case 'assigned':
                        return StockMoveRestore.applyAssigned(state, event)
                    case 'assign-failed':
                        return StockMoveRestore.applyAssignFailed(state, event)
                    case 'shipped':
                        return StockMoveRestore.applyShipped(state, event)
                    case 'arrived':
                        return StockMoveRestore.applyArrived(state, event)
                    case 'shipment-failed':
                        return StockMoveRestore.applyShipmentFailed(state, event)
                    case 'cancelled':
                        return StockMoveRestore.applyCancelled(state, event)
                }
                break
            case 'assigned-move':
                switch (event.tag) {
                    case 'assign-shipped':
                        return StockMoveRestore.applyShipped(state, event)
                    case 'assign-shipment-failed':
                        return StockMoveRestore.applyShipmentFailed(state, event)
                }
                break
            case 'shipped-move':
                switch (event.tag) {
                    case 'arrived':
                        return StockMoveRestore.applyArrived(state, event)
                }
                break
        }

        return state
    }

    private static applyStarted(state: NothingStockMove, 
        event: StartedEvent): StockMove {

        const { tag, ...info } = event

        return { tag: 'draft-move', info }
    }

    private static applyCancelled(state: DraftStockMove, 
        _event: CancelledEvent): StockMove {

        const { info } = state

        return { tag: 'cancelled-move', info }
    }

    private static applyAssigned(state: DraftStockMove, 
        event: AssignedEvent): StockMove {

        const { info } = state
        const { assigned } = event

        return { tag: 'assigned-move', info, assigned }
    }

    private static applyAssignFailed(state: DraftStockMove, 
        _event: AssignFailedEvent): StockMove {

        const { info } = state

        return { tag: 'assign-failed-move', info }
    }

    private static applyShipped(
        state: DraftStockMove | AssignedStockMove, 
        event: ShippedEvent | AssignShippedEvent): StockMove {

        const { info } = state
        const { outgoing } = event

        return { tag: 'shipped-move', info, outgoing }
    }

    private static applyShipmentFailed(
        state: DraftStockMove | AssignedStockMove, 
        _event: ShipmentFailedEvent | AssignShipmentFailedEvent): StockMove {

        const { info } = state

        return { tag: 'shipment-failed-move', info }
    }

    private static applyArrived(
        state: DraftStockMove | ShippedStockMove, 
        event: ArrivedEvent): StockMove {

        const { info } = state
        const { incoming } = event

        return { tag: 'arrived-move', info, incoming }
    }
}

export class StockRestore {
    static restore(state: Stock, events: StockMoveEvent[]): Stock {
        return events.reduce(StockRestore.apply, state)
    }

    private static apply(state: Stock, event: StockMoveEvent): Stock {
        if (state.tag == 'managed') {
            const { qty, assigned } = state

            switch (event.tag) {
                case 'assigned':
                    if (StockFunc.isTarget(state, event.item, event.from)) {
                        return StockFunc.update(
                            state, 
                            qty, 
                            assigned + event.assigned
                        )
                    }
                    break
                case 'shipped':
                    if (StockFunc.isTarget(state, event.item, event.from)) {
                        return StockFunc.update(
                            state, 
                            qty - event.outgoing, 
                            assigned
                        )
                    }
                    break
                case 'assign-shipped':
                    if (StockFunc.isTarget(state, event.item, event.from)) {
                        return StockFunc.update(
                            state, 
                            qty - event.outgoing, 
                            assigned - event.assigned
                        )
                    }
                    break
                case 'assign-shipment-failed':
                    if (StockFunc.isTarget(state, event.item, event.from)) {
                        return StockFunc.update(
                            state, 
                            qty, 
                            assigned - event.assigned
                        )
                    }
                    break
                case 'arrived':
                    if (StockFunc.isTarget(state, event.item, event.to)) {
                        return StockFunc.update(
                            state, 
                            qty + event.incoming, 
                            assigned
                        )
                    }
                    break
            }
        }

        return state
    }
}
