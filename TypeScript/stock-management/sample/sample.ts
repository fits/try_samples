import { v4 as uuidv4 } from 'uuid'

type ItemCode = string
type LocationCode = string
type Quantity = number
type EventId = string
type EventDate = Date
type StockMoveId = string
type Revision = number

const genEventId = (): EventId => `event-${uuidv4()}`

interface Event {
    readonly eventId: EventId
    readonly date: EventDate
}

interface StockMoveEventBase extends Event {
    readonly moveId: StockMoveId
    readonly item: ItemCode
}

class Opened implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/opened' = 'stock-move-event/opened'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly qty: Quantity,
        readonly from: LocationCode,
        readonly to: LocationCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

class Assigned implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/assigned' = 'stock-move-event/assigned'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly assignedQty: Quantity,
        readonly assignedLocation: LocationCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

class AssignFailed implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/assign-failed' = 'stock-move-event/assign-failed'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly assignFailedQty: Quantity,
        readonly assignFailedLocation: LocationCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

class AssignShipped implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/assign-shipped' = 'stock-move-event/assign-shipped'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly assignedQty: Quantity,
        readonly assignedLocation: LocationCode,
        readonly shippedQty: Quantity,
        readonly shippedLocation: LocationCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

class AssignShipFailed implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/assign-ship-failed' = 'stock-move-event/assign-ship-failed'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly assignedQty: Quantity,
        readonly assignedLocation: LocationCode,
        readonly shipFailedLocation: LocationCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

class Shipped implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/shipped' = 'stock-move-event/shipped'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly shippedQty: Quantity,
        readonly shippedLocation: LocationCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

class ShipFailed implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/ship-failed' = 'stock-move-event/ship-failed'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly shipFailedLocation: LocationCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

class Arrived implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/arrived' = 'stock-move-event/arrived'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly arrivedQty: Quantity,
        readonly arrivedLocation: LocationCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

class Cancelled implements StockMoveEventBase {
    readonly _tag: 'stock-move-event/cancelled' = 'stock-move-event/cancelled'

    constructor(
        readonly moveId: StockMoveId,
        readonly item: ItemCode,
        readonly eventId: EventId = genEventId(),
        readonly date: EventDate = new Date()
    ){}
}

type StockMoveEvent = Opened | 
                        Assigned | AssignFailed | 
                        Shipped | ShipFailed | AssignShipped | AssignShipFailed | 
                        Arrived | Cancelled

class StockMoveOrder {
    constructor(
        readonly item: ItemCode,
        readonly qty: Quantity,
        readonly from: LocationCode,
        readonly to: LocationCode
    ){}
}

interface EventSourcing<S, E> {
    applyEvent(event: E): S
}

interface StateTransition<S, A, E, P> {
    transit(action: A, params: P): [S, E?]
}

type StockFind = (ItemCode, LocationCode) => Stock

interface StockMoveBase
    extends EventSourcing<StockMove, StockMoveEvent>, 
            StateTransition<StockMove, StockMoveAction, StockMoveEvent, StockFind> {

    readonly moveId: StockMoveId
}

interface ActiveStockMoveBase extends StockMoveBase {
    readonly revision: Revision
    readonly order: StockMoveOrder
}

class EmptyStockMove implements StockMoveBase {
    readonly _tag: 'stock-move/empty' = 'stock-move/empty'
    readonly moveId: StockMoveId = ''

    transit(act: StockMoveAction): [StockMove, StockMoveEvent?] {
        switch (act._tag) {
            case 'stock-move-action/open':
                if (act.qty > 0) {
                    const event = new Opened(this.genMoveId(), act.item, act.qty, act.from, act.to)
                    const newState = this.applyEvent(event)

                    if (newState._tag != this._tag) {
                        return [newState, event]
                    }
                }
        }
        return [this]
    }

    applyEvent(event: StockMoveEvent): StockMove {
        switch (event._tag) {
            case 'stock-move-event/opened':
                return new OpenedStockMove(
                    event.moveId, 1,
                    new StockMoveOrder(event.item, event.qty, event.from, event.to)
                )
        }
        return this
    }

    private genMoveId(): StockMoveId {
        return `move-${uuidv4()}`
    }
}

class OpenedStockMove implements ActiveStockMoveBase {
    readonly _tag: 'stock-move/opened' = 'stock-move/opened'

    constructor(
        readonly moveId: StockMoveId,
        readonly revision: Revision,
        readonly order: StockMoveOrder) {}

    transit(act: StockMoveAction, stockFind: StockFind): [StockMove, StockMoveEvent?] {
        const event = this.toEvent(act, stockFind)

        if (event != null) {
            const st = this.applyEvent(event)

            if (st._tag != this._tag) {
                return [st, event]
            }
        }

        return [this]
    }

    applyEvent(event: StockMoveEvent): StockMove {
        switch (event._tag) {
            case 'stock-move-event/assigned':
                return new AssignedStockMove(
                    this.moveId, this.revision + 1, this.order,
                    event.assignedQty, event.assignedLocation
                )
            case 'stock-move-event/assign-failed':
                return new AssignFailedStockMove(
                    this.moveId, this.revision + 1, this.order,
                    event.assignFailedQty, event.assignFailedLocation
                )
            case 'stock-move-event/shipped':
                return new ShippedStockMove(
                    this.moveId, this.revision + 1, this.order,
                    event.shippedQty, event.shippedLocation
                )
            case 'stock-move-event/ship-failed':
                return new ShipFailedStockMove(
                    this.moveId, this.revision + 1, this.order, event.shipFailedLocation
                )
            case 'stock-move-event/cancelled':
                return new CancelledStockMove(
                    this.moveId, this.revision + 1, this.order
                )
        }
        return this
    }

    private toEvent(act: StockMoveAction, stockFind: StockFind): StockMoveEvent | undefined {
        switch (act._tag) {
            case 'stock-move-action/assign':
                const stock = stockFind(this.order.item, this.order.from)

                if (stock.availableQty > 0 && stock.availableQty >= this.order.qty) {
                    return new Assigned(this.moveId, this.order.item, 
                                        this.order.qty, this.order.from)
                }
                else {
                    return new AssignFailed(this.moveId, this.order.item, 
                                            this.order.qty, this.order.from)
                }
            case 'stock-move-action/shipped-report':
                if (act.shippedQty > 0) {
                    return new Shipped(this.moveId, this.order.item, 
                                        act.shippedQty, act.shippedLocation)
                }
                else {
                    return new ShipFailed(this.moveId, this.order.item, act.shippedLocation)
                }
            case 'stock-move-action/cancel':
                return new Cancelled(this.moveId, this.order.item)
        }

        return undefined
    }
}

class AssignedStockMove implements ActiveStockMoveBase {
    readonly _tag: 'stock-move/assigned' = 'stock-move/assigned'

    constructor(
        readonly moveId: StockMoveId,
        readonly revision: Revision,
        readonly order: StockMoveOrder,
        readonly assignedQty: Quantity,
        readonly assignedLocation: LocationCode) {}

    transit(act: StockMoveAction): [StockMove, StockMoveEvent?] {
        switch (act._tag) {
            case 'stock-move-action/shipped-report':
                const event = act.shippedQty > 0 ?
                    new AssignShipped(this.moveId, this.order.item, 
                        this.assignedQty, this.assignedLocation, 
                        act.shippedQty, act.shippedLocation) :
                    new AssignShipFailed(this.moveId, this.order.item, 
                        this.assignedQty, this.assignedLocation, act.shippedLocation)

                const newState = this.applyEvent(event)

                if (newState._tag != this._tag) {
                    return [newState, event]
                }
        }
        return [this]
    }

    applyEvent(event: StockMoveEvent): StockMove {
        switch (event._tag) {
            case 'stock-move-event/assign-shipped':
                return new ShippedStockMove(
                    this.moveId, this.revision + 1, this.order,
                    event.shippedQty, event.shippedLocation
                )
            case 'stock-move-event/assign-ship-failed':
                return new ShipFailedStockMove(
                    this.moveId, this.revision + 1, this.order,
                    event.shipFailedLocation
                )
        }
        return this
    }
}

class AssignFailedStockMove implements ActiveStockMoveBase {
    readonly _tag: 'stock-move/assign-failed' = 'stock-move/assign-failed'

    constructor(
        readonly moveId: StockMoveId,
        readonly revision: Revision,
        readonly order: StockMoveOrder,
        readonly assignFailedQty: Quantity,
        readonly assignFailedLocation: LocationCode) {}

    transit(): [StockMove, StockMoveEvent?] {
        return [this]
    }

    applyEvent(): StockMove {
        return this
    }
}

class ShippedStockMove implements ActiveStockMoveBase {
    readonly _tag: 'stock-move/shipped' = 'stock-move/shipped'

    constructor(
        readonly moveId: StockMoveId,
        readonly revision: Revision,
        readonly order: StockMoveOrder,
        readonly shippedQty: Quantity,
        readonly shippedLocation: LocationCode) {}

    transit(act: StockMoveAction): [StockMove, StockMoveEvent?] {
        switch (act._tag) {
            case 'stock-move-action/arrived-report':
                if (act.arrivedQty >= 0) {
                    const event = new Arrived(this.moveId, this.order.item, 
                        act.arrivedQty, act.arrivedLocation)

                    const newState = this.applyEvent(event)

                    if (newState._tag != this._tag) {
                        return [newState, event]
                    }
                }
        }
        return [this]
    }

    applyEvent(event: StockMoveEvent): StockMove {
        switch (event._tag) {
            case 'stock-move-event/arrived':
                return new ArrivedStockMove(
                    this.moveId, this.revision + 1, this.order,
                    event.arrivedQty, event.arrivedLocation,
                    this.shippedQty, this.shippedLocation
                )
        }
        return this
    }
}

class ShipFailedStockMove implements ActiveStockMoveBase {
    readonly _tag: 'stock-move/ship-failed' = 'stock-move/ship-failed'

    constructor(
        readonly moveId: StockMoveId,
        readonly revision: Revision,
        readonly order: StockMoveOrder,
        readonly shipFailedLocation: LocationCode) {}

    transit(): [StockMove, StockMoveEvent?] {
        return [this]
    }

    applyEvent(): StockMove {
        return this
    }
}

class ArrivedStockMove implements ActiveStockMoveBase {
    readonly _tag: 'stock-move/arrived' = 'stock-move/arrived'

    constructor(
        readonly moveId: StockMoveId,
        readonly revision: Revision,
        readonly order: StockMoveOrder,
        readonly arrivedQty: Quantity,
        readonly arrivedLocation: LocationCode,
        readonly shippedQty: Quantity,
        readonly shippedLocation: LocationCode) {}

    transit(): [StockMove, StockMoveEvent?] {
        return [this]
    }

    applyEvent(): StockMove {
        return this
    }
}

class CancelledStockMove implements ActiveStockMoveBase {
    readonly _tag: 'stock-move/cancelled' = 'stock-move/cancelled'

    constructor(
        readonly moveId: StockMoveId,
        readonly revision: Revision,
        readonly order: StockMoveOrder) {}

    transit(): [StockMove, StockMoveEvent?] {
        return [this]
    }

    applyEvent(): StockMove {
        return this
    }
}

type StockMove = EmptyStockMove | OpenedStockMove | 
                    AssignedStockMove | AssignFailedStockMove |
                    ShippedStockMove | ShipFailedStockMove | ArrivedStockMove |
                    CancelledStockMove

class Open {
    readonly _tag: 'stock-move-action/open' = 'stock-move-action/open'

    constructor(
        readonly item: ItemCode,
        readonly qty: Quantity,
        readonly from: LocationCode,
        readonly to: LocationCode
    ){}
}

class Assign {
    readonly _tag: 'stock-move-action/assign' = 'stock-move-action/assign'

    constructor(readonly moveId: StockMoveId){}
}

class Cancel {
    readonly _tag: 'stock-move-action/cancel' = 'stock-move-action/cancel'

    constructor(readonly moveId: StockMoveId){}
}

class ShippedReport {
    readonly _tag: 'stock-move-action/shipped-report' = 'stock-move-action/shipped-report'

    constructor(
        readonly moveId: StockMoveId, 
        readonly shippedQty: Quantity, 
        readonly shippedLocation: LocationCode
    ){}
}

class ArrivedReport {
    readonly _tag: 'stock-move-action/arrived-report' = 'stock-move-action/arrived-report'

    constructor(
        readonly moveId: StockMoveId,
        readonly arrivedQty: Quantity,
        readonly arrivedLocation: LocationCode
    ){}
}

type StockMoveAction = Open | Assign | Cancel | 
                        ShippedReport | ArrivedReport

class Stock implements EventSourcing<Stock, StockMoveEvent> {
    readonly availableQty: Quantity = this.actualQty - this.assignedQty

    constructor(
        readonly item: ItemCode,
        readonly location: LocationCode,
        readonly actualQty: Quantity = 0,
        readonly assignedQty: Quantity = 0
    ){}

    applyEvent(event: StockMoveEvent): Stock {
        switch (event._tag) {
            case 'stock-move-event/assigned':
                if (event.item == this.item && event.assignedLocation == this.location) {
                    return this.update(0, event.assignedQty)
                }
                break
            case 'stock-move-event/assign-shipped':
                if (event.item == this.item && 
                    (event.assignedLocation == this.location || 
                        event.shippedLocation == this.location)) {

                    return this.update(
                        (event.shippedLocation == this.location) ? -event.shippedQty : 0,
                        (event.assignedLocation == this.location) ? -event.assignedQty : 0
                    )
                }
                break
            case 'stock-move-event/assign-ship-failed':
                if (event.item == this.item && event.assignedLocation == this.location) {
                    return this.update(0, -event.assignedQty)
                }
                break
            case 'stock-move-event/shipped':
                if (event.item == this.item && event.shippedLocation == this.location) {
                    return this.update(-event.shippedQty, 0)
                }
                break
            case 'stock-move-event/arrived':
                if (event.item == this.item && event.arrivedLocation == this.location) {
                    return this.update(event.arrivedQty, 0)
                }
                break
        }
        return this
    }

    private update(diffActualQty: Quantity, diffAssignedQty: Quantity): Stock {
        return new Stock(this.item, this.location, 
                            this.actualQty + diffActualQty, this.assignedQty + diffAssignedQty)
    }
}

interface StockMoveService {
    status(id: StockMoveId): StockMove | undefined
    action(act: StockMoveAction): StockMove | undefined
    stock(item: ItemCode, location: LocationCode): Stock
}

type EventNo = number

interface EventStore<K, E> {
    save(key: K, event: E): EventNo

    load(key: K, skipNo: EventNo): Array<E>
    loadAll(skipNo: EventNo): Array<E>
}

class MemoryEventStore<K, E> implements EventStore<K, E> {
    private store: Array<E> = []
    private mapStore: Map<K, Array<[EventNo, E]>> = new Map()

    save(key: K, event: E): EventNo {
        const eventNo = this.store.push(event)

        const keyEvents = this.mapStore.get(key) ?? this.mapStore.set(key, []).get(key)

        keyEvents?.push([eventNo, event])

        return eventNo
    }

    load(key: K, skipNo: EventNo = 0): Array<E> {
        return (this.mapStore.get(key) ?? [])
                    .filter(([n, e]) => n > skipNo)
                    .map(([n, e]) => e)
    }

    loadAll(skipNo: EventNo = 0): Array<E> {
        return this.store.filter((_, i) => i >= skipNo)
    }
}

type StockMoveResult = StockMove | undefined

class BasicStockMoveService implements StockMoveService {
    private empty: StockMove = new EmptyStockMove()

    constructor(
        private eventStore: EventStore<StockMoveId, StockMoveEvent>
    ){}

    status(id: StockMoveId): StockMoveResult {
        const res = this.restoreState(id)
        return (res._tag == this.empty._tag) ? undefined : res
    }

    action(act: StockMoveAction): StockMoveResult {
        const state = (act._tag == 'stock-move-action/open') ? 
            this.empty : this.restoreState(act.moveId)

        const [newState, event] = state.transit(act, (i, l) => this.stock(i, l))

        if (event != null) {
            this.eventStore.save(event.moveId, event)
            return newState
        }

        return undefined
    }

    stock(item: ItemCode, location: LocationCode): Stock {
        return this.eventStore.loadAll(0).reduce(
            (acc, ev) => acc.applyEvent(ev),
            new Stock(item, location)
        )
    }

    private restoreState(id: StockMoveId): StockMove {
        return this.eventStore.load(id, 0).reduce(
            (acc, ev) => acc.applyEvent(ev), 
            this.empty
        )
    }
}

const store = new MemoryEventStore<StockMoveId, StockMoveEvent>()
const service = new BasicStockMoveService(store)

const a1 = service.action(new Open('item1', 1, 'loc1', 'loc2'))
console.log(a1)
const a2 = service.action(new Assign(a1?.moveId ?? ''))
console.log(a2)

const b1 = service.action(new Open('item1', 10, 'adjust1', 'loc1'))
console.log(b1)
const b2 = service.action(new ShippedReport(b1?.moveId ?? '', 10, 'adjust1'))
console.log(b2)
const b3 = service.action(new ArrivedReport(b1?.moveId ?? '', 10, 'loc1'))
console.log(b3)

console.log(service.stock('item1', 'adjust1'))
console.log(service.stock('item1', 'loc1'))

const c1 = service.action(new Open('item1', 1, 'loc1', 'loc2'))
console.log(c1)
const c2 = service.action(new Assign(c1?.moveId ?? ''))
console.log(c2)
console.log(service.stock('item1', 'loc1'))
const c3 = service.action(new ShippedReport(c1?.moveId ?? '', 1, 'loc1'))
console.log(c3)
const c4 = service.action(new ArrivedReport(c1?.moveId ?? '', 1, 'loc2'))
console.log(c4)

console.log(service.stock('item1', 'adjust1'))
console.log(service.stock('item1', 'loc1'))
console.log(service.stock('item1', 'loc2'))
