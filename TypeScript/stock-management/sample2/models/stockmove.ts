
import { 
    ItemCode, LocationCode, Quantity, 
    StockMoveEvent, StockMoveEventShipped, StockMoveEventAssignShipped 
} from './events'

interface StockUnmanaged {
    tag: 'stock.unmanaged'
    item: ItemCode
    location: LocationCode
}

interface StockManaged {
    tag: 'stock.managed'
    item: ItemCode
    location: LocationCode
    qty: Quantity
    assigned: Quantity
}

export type Stock = StockUnmanaged | StockManaged

export class StockAction {
    static newUnmanaged(item: ItemCode, location: LocationCode): Stock {
        return {
            tag: 'stock.unmanaged',
            item,
            location
        }
    }

    static newManaged(item: ItemCode, location: LocationCode): Stock {
        return {
            tag: 'stock.managed',
            item,
            location,
            qty: 0,
            assigned: 0
        }
    }

    static isSufficient(stock: Stock, qty: Quantity): boolean {
        switch (stock.tag) {
            case 'stock.unmanaged':
                return true
            case 'stock.managed':
                return qty + Math.max(0, stock.assigned) <= Math.max(0, stock.qty)
        }
    }
}

export class StockRestore {
    static restore(state: Stock, events: StockMoveEvent[]): Stock {
        return events.reduce(StockRestore.applyTo, state)
    }

    private static applyTo(state: Stock, event: StockMoveEvent): Stock {
        if (state.tag == 'stock.managed') {
            switch (event.tag) {
                case 'stock-move-event.assigned':
                    if (state.item == event.item && state.location == event.from) {
                        return StockRestore.updateAssigned(
                            state, 
                            state.assigned + event.assigned
                        )
                    }
                    break
                case 'stock-move-event.assign-shipped':
                    if (state.item == event.item && state.location == event.from) {
                        return StockRestore.updateStock(
                            state,
                            state.qty - event.outgoing,
                            state.assigned - event.assigned
                        )
                    }
                    break
                case 'stock-move-event.shipped':
                    if (state.item == event.item && state.location == event.from) {
                        return StockRestore.updateQty(
                            state,
                            state.qty - event.outgoing
                        )
                    }
                    break
                case 'stock-move-event.arrived':
                    if (state.item == event.item && state.location == event.to) {
                        return StockRestore.updateQty(
                            state,
                            state.qty + event.incoming
                        )
                    }
                    break
            }
        }
        return state
    }

    private static updateStock(stock: Stock, qty: Quantity, assigned: Quantity): Stock {
        switch (stock.tag) {
            case 'stock.unmanaged':
                return stock
            case 'stock.managed':
                return {
                    tag: stock.tag,
                    item: stock.item,
                    location: stock.location,
                    qty,
                    assigned
                }
        }
    }

    private static updateQty(stock: Stock, qty: Quantity): Stock {
        const assigned = (stock.tag == 'stock.managed') ? stock.assigned : 0
        return StockRestore.updateStock(stock, qty, assigned)
    }

    private static updateAssigned(stock: Stock, assigned: Quantity): Stock {
        const qty = (stock.tag == 'stock.managed') ? stock.qty : 0
        return StockRestore.updateStock(stock, qty, assigned)
    }
}

interface StockMoveInfo {
    item: ItemCode
    qty: Quantity
    from: LocationCode
    to: LocationCode
}

interface StockMoveNothing {
    tag: 'stock-move.nothing'
}

interface StockMoveDraft {
    tag: 'stock-move.draft'
    info: StockMoveInfo
}

interface StockMoveCompleted {
    tag: 'stock-move.completed'
    info: StockMoveInfo
    outgoing: Quantity
    incoming: Quantity
}

interface StockMoveCancelled {
    tag: 'stock-move.cancelled'
    info: StockMoveInfo
}

interface StockMoveAssigned {
    tag: 'stock-move.assigned'
    info: StockMoveInfo
    assigned: Quantity
}

interface StockMoveShipped {
    tag: 'stock-move.shipped'
    info: StockMoveInfo
    outgoing: Quantity
}

interface StockMoveArrived {
    tag: 'stock-move.arrived'
    info: StockMoveInfo
    outgoing: Quantity
    incoming: Quantity
}

interface StockMoveAssignFailed {
    tag: 'stock-move.assign-failed'
    info: StockMoveInfo
}

interface StockMoveShipmentFailed {
    tag: 'stock-move.shipment-failed'
    info: StockMoveInfo
}

export type StockMove = 
    StockMoveNothing | StockMoveDraft | StockMoveCompleted | 
    StockMoveCancelled | StockMoveAssigned | StockMoveShipped |
    StockMoveArrived | StockMoveAssignFailed | StockMoveShipmentFailed


export type StockMoveResult = [StockMove, StockMoveEvent] | undefined

export class StockMoveAction {
    static initialState(): StockMove {
        return { tag: 'stock-move.nothing' }
    }

    static start(state: StockMove, item: ItemCode, qty: Quantity, 
        from: LocationCode, to: LocationCode): StockMoveResult {

        if (qty < 1) {
            return undefined
        }

        const event: StockMoveEvent = {
            tag: 'stock-move-event.started',
            item,
            qty,
            from,
            to
        }

        return StockMoveAction.applyTo(state, event)
    }

    static assign(state: StockMove, stock: Stock): StockMoveResult {
        const info = StockMoveAction.info(state)

        if (info && info.item == stock.item && info.from == stock.location) {
            const assigned = 
                (stock && StockAction.isSufficient(stock, info.qty)) ? info.qty : 0
            
            const event: StockMoveEvent = {
                tag: 'stock-move-event.assigned',
                item: info.item,
                from: info.from,
                assigned
            }

            return StockMoveAction.applyTo(state, event)
        }

        return undefined
    }

    static ship(state: StockMove, outgoing: Quantity): StockMoveResult {
        if (outgoing < 0) {
            return undefined
        }

        const event = StockMoveAction.toShippedEvent(state, outgoing)

        return event ? StockMoveAction.applyTo(state, event) : undefined
    }

    static arrive(state: StockMove, incoming: Quantity): StockMoveResult {
        if (incoming < 0) {
            return undefined
        }

        const info = StockMoveAction.info(state)

        if (info) {
            const event: StockMoveEvent = {
                tag: 'stock-move-event.arrived',
                item: info.item,
                to: info.to,
                incoming
            }

            return StockMoveAction.applyTo(state, event)
        }
        return undefined
    }

    static complete(state: StockMove): StockMoveResult {
        const event: StockMoveEvent = {
            tag: 'stock-move-event.completed'
        }
        return StockMoveAction.applyTo(state, event)
    }

    static cancel(state: StockMove): StockMoveResult {
        const event: StockMoveEvent = {
            tag: 'stock-move-event.cancelled'
        }
        return StockMoveAction.applyTo(state, event)
    }

    static info(state: StockMove) {
        if (state.tag != 'stock-move.nothing') {
            return state.info
        }

        return undefined
    }

    private static applyTo(state: StockMove, event: StockMoveEvent): StockMoveResult {
        const nextState = StockMoveRestore.restore(state, [event])

        return (nextState != state) ? [nextState, event] : undefined
    }

    private static toShippedEvent(state: StockMove, outgoing: number): StockMoveEvent | undefined {

        const info = StockMoveAction.info(state)

        if (info) {
            if (state.tag == 'stock-move.assigned') {
                return {
                    tag: 'stock-move-event.assign-shipped',
                    item: info.item,
                    from: info.from,
                    assigned: state.assigned,
                    outgoing
                }
            }
            else {
                return {
                    tag: 'stock-move-event.shipped',
                    item: info.item,
                    from: info.from,
                    outgoing
                }
            }
        }
        return undefined
    }
}

export class StockMoveRestore {
    static restore(state: StockMove, events: StockMoveEvent[]): StockMove {
        return events.reduce(StockMoveRestore.applyTo, state)
    }

    private static applyTo(state: StockMove, event: StockMoveEvent): StockMove {
        switch (state.tag) {
            case 'stock-move.nothing':
                if (event.tag == 'stock-move-event.started') {
                    return {
                        tag: 'stock-move.draft',
                        info: {
                            item: event.item,
                            qty: event.qty,
                            from: event.from,
                            to: event.to
                        }
                    }
                }
                break
            case 'stock-move.draft':
                return StockMoveRestore.applyEventToDraft(state, event)
            case 'stock-move.assigned':
                if (event.tag == 'stock-move-event.assign-shipped') {
                    return StockMoveRestore.applyShipped(state, event)
                }
                break
            case 'stock-move.shipped':
                if (event.tag == 'stock-move-event.arrived' &&
                    state.info.item == event.item && 
                    state.info.to == event.to) {

                    return {
                        tag: 'stock-move.arrived',
                        info: state.info,
                        outgoing: state.outgoing,
                        incoming: event.incoming
                    }
                }
                break
            case 'stock-move.arrived':
                if (event.tag == 'stock-move-event.completed') {
                    return {
                        tag: 'stock-move.completed',
                        info: state.info,
                        outgoing: state.outgoing,
                        incoming: state.incoming
                    }
                }
                break
            case 'stock-move.completed':
            case 'stock-move.cancelled':
            case 'stock-move.assign-failed':
            case 'stock-move.shipment-failed':
                break
        }
        return state
    }

    private static applyShipped(state: StockMoveDraft | StockMoveAssigned, 
        event: StockMoveEventShipped | StockMoveEventAssignShipped): StockMove {

        if (state.info.item == event.item && state.info.from == event.from) {
            if (event.outgoing > 0) {
                return {
                    tag: 'stock-move.shipped',
                    info: state.info,
                    outgoing: event.outgoing
                }
            }
            else {
                return {
                    tag: 'stock-move.shipment-failed',
                    info: state.info
                }
            }
        }
        return state
    }

    private static applyEventToDraft(state: StockMoveDraft, 
        event: StockMoveEvent): StockMove {

        switch (event.tag) {
            case 'stock-move-event.cancelled':
                return {
                    tag: 'stock-move.cancelled',
                    info: state.info
                }
            case 'stock-move-event.assigned':
                if (state.info.item == event.item && state.info.from == event.from) {
                    if (event.assigned > 0) {
                        return {
                            tag: 'stock-move.assigned',
                            info: state.info,
                            assigned: event.assigned
                        }
                    }
                    else {
                        return {
                            tag: 'stock-move.assign-failed',
                            info: state.info
                        }
                    }
                }
                break
            case 'stock-move-event.shipped':
                return StockMoveRestore.applyShipped(state, event)
            case 'stock-move-event.arrived':
                if (state.info.item == event.item && state.info.to == event.to) {
                    return {
                        tag: 'stock-move.arrived',
                        info: state.info,
                        outgoing: 0,
                        incoming: Math.max(event.incoming, 0)
                    }
                }
                break
        }

        return state
    }
}
