import { Free, free, liftF, foldFree } from 'fp-ts-contrib/lib/Free'
import { Do } from 'fp-ts-contrib/lib/Do'
import { Option, option, some, none, map, filterMap, fromNullable, fold } 
    from 'fp-ts/lib/Option'
import { pipe } from 'fp-ts/lib/pipeable'

const URI = 'StockEvent'
type URI = typeof URI

type StockId = string
type Quantity = number

interface Stock {
    readonly id: StockId
    readonly qty: Quantity
}

class Created<A> {
    readonly _URI!: URI
    readonly _A!: A
    readonly _tag: 'Created' = 'Created'

    constructor(readonly id: StockId) {}
}

class Shipped<A> {
    readonly _URI!: URI
    readonly _A!: A
    readonly _tag: 'Shipped' = 'Shipped'

    constructor(readonly id: StockId, readonly shippedQty: Quantity) {}
}

class Arrived<A> {
    readonly _URI!: URI
    readonly _A!: A
    readonly _tag: 'Arrived' = 'Arrived'

    constructor(readonly id: StockId, readonly arrivedQty: Quantity) {}
}

type CommandF<A> = Created<A> | Shipped<A> | Arrived<A>

declare module 'fp-ts/lib/HKT' {
    interface URItoKind<A> {
        StockEvent: CommandF<A>
    }
}

const create = (id: StockId): Free<URI, Stock> => liftF(new Created(id))

const ship = (id: StockId, qty: Quantity): Free<URI, Stock> => 
    liftF(new Shipped(id, qty))

const arrive = (id: StockId, qty: Quantity): Free<URI, Stock> => 
    liftF(new Arrived(id, qty))

const move = (from: StockId, to: StockId, qty: Quantity) =>
    Do(free)
        .bind('f', ship(from, qty))
        .bind('t', arrive(to, qty))
        .return(({f, t}) => [f, t])

const store = {}

const saveToStore = (s: Stock): any => {
    store[s.id] = s
    return s as any
}

const loadFromStore = (id: StockId): Option<Stock> => fromNullable(store[id])

const createStock = (id: StockId) => <Stock>{id: id, qty: 0}
const updateStock = (s: Stock, diffQty: Quantity) => 
    <Stock>{id: s.id, qty: s.qty + diffQty}

const step = <A>(fa: CommandF<A>): Option<A> => {
    switch (fa._tag) {
        case 'Created':
            return pipe(
                some(createStock(fa.id)),
                map(saveToStore)
            )

        case 'Shipped':
            return pipe(
                loadFromStore(fa.id),
                map(s => updateStock(s, -fa.shippedQty)),
                filterMap(s => s.qty >= 0 ? some(s) : none),
                map(saveToStore)
            )

        case 'Arrived':
            return pipe(
                loadFromStore(fa.id),
                map(s => updateStock(s, fa.arrivedQty)),
                map(saveToStore)
            )
    }
}

const program = Do(free)
    .bind('a1', create('stock-a'))
    .bind('b1', create('stock-b'))
    .bind('a2', arrive('stock-a', 20))
    .bind('r1', move('stock-a', 'stock-b', 7))
    .bind('r2', move('stock-a', 'stock-b', 1))
    .return(({ r2 }) => r2)

const program2 = Do(free)
    .bind('c1', create('stock-c'))
    .bind('d1', create('stock-d'))
    .bind('c2', arrive('stock-c', 5))
    .bind('r', move('stock-c', 'stock-d', 6))
    .return(({ r }) => r)

const res = foldFree(option)(step, program)

console.log(res)

fold(() => console.log('fail'), v => console.log(v))(res)

console.log(store)

console.log('-------------')

pipe(
    foldFree(option)(step, program2),
    fold(() => console.log('fail'), v => console.log(v))
)

console.log(store)
