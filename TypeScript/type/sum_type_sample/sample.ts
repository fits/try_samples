
type ItemCode = string
type LocationCode = string
type Quantity = number

interface Created {
    eventType: 'created'
    item: ItemCode
    qty: Quantity
    from: LocationCode
    to: LocationCode
}

interface Shipped {
    eventType: 'shipped'
    item: ItemCode
    location: LocationCode
    shippedQty: Quantity
}

interface Arrived {
    eventType: 'arrived'
    item: ItemCode
    location: LocationCode
    arrivedQty: Quantity
}

type StockEvent = Created | Shipped | Arrived

const showEvent = (event: StockEvent) => {
    switch (event.eventType) {
        case "created":
            return `create: item = ${event.item}, qty = ${event.qty}, from = ${event.from}, to = ${event.to}`
        case "shipped": 
            return `shipped: item = ${event.item}, location = ${event.location}, shippedQty = ${event.shippedQty}`
        case "arrived": 
            return `arrived: item = ${event.item}, location = ${event.location}, shippedQty = ${event.arrivedQty}`
    }
}

function create(item: ItemCode, qty: Quantity, 
    from: LocationCode, to: LocationCode): Created {

    return {
        eventType: 'created', item: item, qty: qty, 
        from: from, to: to
    } as Created
}

function ship(item: ItemCode, loc: LocationCode, qty: Quantity): Shipped {
    return {
        eventType: 'shipped', item: item, location: loc, 
        shippedQty: qty
    } as Shipped
}

function arrive(item: ItemCode, loc: LocationCode, qty: Quantity): Arrived {
    return {
        eventType: 'arrived', item: item, location: loc, 
        arrivedQty: qty
    } as Arrived
}

type CreateFunc = (item: ItemCode, qty: Quantity, 
                    from: LocationCode, to: LocationCode) => Created

const createF: CreateFunc = (item, qty, from, to) => <Created>({
    eventType: 'created', item: item, qty: qty, 
    from: from, to: to
})

const d1 = create('item1', 3, 'loc1', 'loc2')
const d2 = ship('item1', 'loc1', 2)
const d3 = arrive('item1', 'loc2', 1)
const d4 = createF('item2', 5, 'loc1', 'loc2')

const ds = [d1, d2, d3, d4]

ds.forEach(d => {
    console.log(d)
    console.log(showEvent(d))
})
