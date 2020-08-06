
import { 
    ItemCode, 
    LocationCode, 
    Stock,
    StockMoveEvent,
    StockFunc, 
    StockMoveAction, 
    StockMoveFunc, 
    StockMoveRestore, 
    StockRestore
} from './models.ts'

const item1 = 'item-A'

const loc0 = 'adjust-1'
const loc1 = 'store-1'
const loc2 = 'customer-1'

let s0: Stock = StockFunc.newUnmanaged(item1, loc0)
let s1: Stock = StockFunc.newManaged(item1, loc1)
let s2: Stock = StockFunc.newManaged(item1, loc2)

console.log(s0)
console.log(s1)
console.log(s2)

console.log('-----')

const stockFinder = (item: ItemCode, location: LocationCode) => 
    [s0, s1, s2].find(s => 
        s.item == item && s.location == location
    )

const updateStock = (ev: StockMoveEvent) => {
    s0 = StockRestore.restore(s0, [ev])
    s1 = StockRestore.restore(s1, [ev])
    s2 = StockRestore.restore(s2, [ev])

    console.log(`*** s0 = ${JSON.stringify(s0)}, s1 = ${JSON.stringify(s1)}, s2 = ${JSON.stringify(s2)}`)
}

const mv0 = StockMoveFunc.initial()

const a1 = StockMoveAction.start(mv0, item1, 50, loc0, loc1)
console.log(a1)

if (a1) {
    updateStock(a1)

    const mv1 = StockMoveRestore.restore(mv0, [a1])
    console.log(mv1)

    const a2 = StockMoveAction.arrive(mv1, 50)
    console.log(a2)

    if (a2) {
        updateStock(a2)

        const mv2 = StockMoveRestore.restore(mv1, [a2])
        console.log(mv2)
    }
}

console.log('-----')

const b1 = StockMoveAction.start(mv0, item1, 2, loc1, loc2)
console.log(b1)

if (b1) {
    updateStock(b1)

    const mv1 = StockMoveRestore.restore(mv0, [b1])
    console.log(mv1)

    const b2 = StockMoveAction.assign(mv1, stockFinder)
    console.log(b2)

    if (b2) {
        updateStock(b2)

        const mv2 = StockMoveRestore.restore(mv1, [b2])
        console.log(mv2)

        const b3 = StockMoveAction.ship(mv2, 2)
        console.log(b3)

        if (b3) {
            updateStock(b3)

            const mv3 = StockMoveRestore.restore(mv2, [b3])
            console.log(mv3)

            const b4 = StockMoveAction.arrive(mv3, 2)
            console.log(b4)

            if (b4) {
                updateStock(b4)

                const mv4 = StockMoveRestore.restore(mv3, [b4])
                console.log(mv4)
            }
        }
    }
}

console.log('-----')

const c1 = StockMoveAction.start(mv0, item1, 5, loc1, loc2)
console.log(c1)

if (c1) {
    updateStock(c1)

    const mv1 = StockMoveRestore.restore(mv0, [c1])
    console.log(mv1)

    const c2 = StockMoveAction.cancel(mv1)
    console.log(c2)

    if (c2) {
        updateStock(c2)

        const mv2 = StockMoveRestore.restore(mv1, [c2])
        console.log(mv2)
    }
}

console.log('-----')

const d1 = StockMoveAction.start(mv0, item1, 50, loc1, loc2)
console.log(d1)

if (d1) {
    updateStock(d1)

    const mv1 = StockMoveRestore.restore(mv0, [d1])
    console.log(mv1)

    const d2 = StockMoveAction.assign(mv1, stockFinder)
    console.log(d2)

    if (d2) {
        updateStock(d2)

        const mv2 = StockMoveRestore.restore(mv1, [d2])
        console.log(mv2)
    }
}

console.log('-----')

const e1 = StockMoveAction.start(mv0, item1, 1, loc1, loc2)
console.log(e1)

if (e1) {
    updateStock(e1)

    const mv1 = StockMoveRestore.restore(mv0, [e1])
    console.log(mv1)

    const e2 = StockMoveAction.ship(mv1, 0)
    console.log(e2)

    if (e2) {
        updateStock(e2)

        const mv2 = StockMoveRestore.restore(mv1, [e2])
        console.log(mv2)
    }
}