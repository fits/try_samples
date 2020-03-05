import { Free, free, liftF, foldFree } from 'fp-ts-contrib/lib/Free'
import { Do } from 'fp-ts-contrib/lib/Do'
import { Identity, identity as id } from 'fp-ts/lib/Identity'

const URI = 'Event'
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

    constructor(readonly stock: Stock, readonly shippedQty: Quantity) {}
}

class Arrived<A> {
    readonly _URI!: URI
    readonly _A!: A
    readonly _tag: 'Arrived' = 'Arrived'

    constructor(readonly stock: Stock, readonly arrivedQty: Quantity) {}
}

type CommandF<A> = Created<A> | Shipped<A> | Arrived<A>

declare module 'fp-ts/lib/HKT' {
    interface URItoKind<A> {
        Event: CommandF<A>
    }
}

const create = (id: StockId): Free<URI, Stock> => liftF(new Created(id))

const ship = (stock: Stock, qty: Quantity): Free<URI, Stock> => 
    liftF(new Shipped(stock, qty))

const arrive = (stock: Stock, qty: Quantity): Free<URI, Stock> => 
    liftF(new Arrived(stock, qty))

const createStock = (id: StockId) => <Stock>{id: id, qty: 0}
const updateStock = (s: Stock, diffQty: Quantity) => 
    <Stock>{id: s.id, qty: s.qty + diffQty}

const step = <A>(fa: CommandF<A>): Identity<A> => {
    switch (fa._tag) {
        case 'Created':
            return id.of(createStock(fa.id) as any)
        case 'Shipped':
            return id.of(updateStock(fa.stock, -fa.shippedQty) as any)
        case 'Arrived':
            return id.of(updateStock(fa.stock, fa.arrivedQty) as any)
    }
}

const program = Do(free)
    .bind('s1', create('stock1'))
    .bindL('s2', ({ s1 }) => arrive(s1, 10))
    .bindL('s3', ({ s2 }) => arrive(s2, 5))
    .bindL('s4', ({ s3 }) => ship(s3, 2))
    .bindL('s5', ({ s4 }) => ship(s4, 1))
    .return(({ s1, s5 }) => [s1, s5])

console.log(program)

const res = foldFree(id)(step, program)

console.log(res)
