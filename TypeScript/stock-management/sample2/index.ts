
import { ApolloServer, gql } from 'apollo-server'
import { v4 as uuidv4 } from 'uuid'
import { MongoClient, Collection } from 'mongodb'

import {
    ItemCode, LocationCode,
    StockMoveAction, StockMoveRestore, StockMove, StockMoveEvent, 
    StockMoveResult,
    StockAction, StockRestore, Stock
} from './models'

const mongoUrl = 'mongodb://localhost'
const dbName = 'stockmoves'
const colName = 'events'
const stocksColName = 'stocks'

type MoveId = string
type Revision = number

interface StoredEvent {
    move_id: MoveId
    revision: Revision
    item: ItemCode
    from: LocationCode
    to: LocationCode
    event: StockMoveEvent
}

interface RestoredStockMove {
    state: StockMove
    revision: Revision
}

class Store {
    private eventsCol: Collection
    private stocksCol: Collection

    constructor(eventsCol: Collection, stocksCol: Collection) {
        this.eventsCol = eventsCol
        this.stocksCol = stocksCol
    }

    async loadStock(item: ItemCode, location: LocationCode): Promise<Stock | undefined> {
        const id = this.stockId(item, location)
        const stock = await this.stocksCol.findOne({ _id: id })

        if (!stock) {
            return undefined
        }

        const query = {
            '$and': [
                { item },
                { '$or': [
                    { from: location },
                    { to: location }
                ]}
            ]
        }

        const events = await this.eventsCol
            .find(query)
            .map(r => r.event)
            .toArray()

        return StockRestore.restore(stock, events)
    }

    async saveStock(stock: Stock): Promise<void> {
        const id = this.stockId(stock.item, stock.location)

        const res = await this.stocksCol.updateOne(
            { _id: id },
            { '$setOnInsert': stock },
            { upsert: true }
        )

        if (res.upsertedCount == 0) {
            return Promise.reject('conflict stock')
        }
    }

    async loadMove(moveId: MoveId): Promise<RestoredStockMove | undefined> {
        const events: StoredEvent[] = await this.eventsCol
            .find({ move_id: moveId })
            .sort({ revision: 1 })
            .toArray()

        const state = StockMoveAction.initialState()
        const revision = events.reduce((acc, e) => Math.max(acc, e.revision), 0)

        const res = StockMoveRestore.restore(state, events.map(e => e.event))

        return (res == state) ? undefined : { state: res, revision }
    }

    async saveEvent(event: StoredEvent): Promise<void> {
        const res = await this.eventsCol.updateOne(
            { move_id: event.move_id, revision: event.revision },
            { '$setOnInsert': event },
            { upsert: true }
        )

        if (res.upsertedCount == 0) {
            return Promise.reject(`conflict event revision=${event.revision}`)
        }
    }

    private stockId(item: ItemCode, location: LocationCode): string {
        return `${item}/${location}`
    }
}

const typeDefs = gql(`
    type StockMoveInfo {
        item: ID!
        qty: Int!
        from: ID!
        to: ID!
    }

    interface StockMove {
        id: ID!
        info: StockMoveInfo!
    }

    type DraftStockMove implements StockMove {
        id: ID!
        info: StockMoveInfo!
    }

    type CompletedStockMove implements StockMove {
        id: ID!
        info: StockMoveInfo!
        outgoing: Int!
        incoming: Int!
    }

    type CancelledStockMove implements StockMove {
        id: ID!
        info: StockMoveInfo!
    }

    type AssignedStockMove implements StockMove {
        id: ID!
        info: StockMoveInfo!
        assigned: Int!
    }

    type ShippedStockMove implements StockMove {
        id: ID!
        info: StockMoveInfo!
        outgoing: Int!
    }

    type ArrivedStockMove implements StockMove {
        id: ID!
        info: StockMoveInfo!
        outgoing: Int!
        incoming: Int!
    }

    type AssignFailedStockMove implements StockMove {
        id: ID!
        info: StockMoveInfo!
    }

    type ShipmentFailedStockMove implements StockMove {
        id: ID!
        info: StockMoveInfo!
    }

    interface Stock {
        item: ID!
        location: ID!
    }

    type UnmanagedStock implements Stock {
        item: ID!
        location: ID!
    }

    type ManagedStock implements Stock {
        item: ID!
        location: ID!
        qty: Int!
        assigned: Int!
    }

    input CreateStockInput {
        item: ID!
        location: ID!
    }

    input StartMoveInput {
        item: ID!
        qty: Int!
        from: ID!
        to: ID!
    }

    type Query {
        findStock(item: ID!, location: ID!): Stock
        findMove(id: ID!): StockMove
    }

    type Mutation {
        createManaged(input: CreateStockInput!): ManagedStock
        createUnmanaged(input: CreateStockInput!): UnmanagedStock

        start(input: StartMoveInput!): StockMove
        assign(id: ID!): StockMove
        ship(id: ID!, outgoing: Int!): StockMove
        arrive(id: ID!, incoming: Int!): StockMove
        complete(id: ID!): StockMove
        cancel(id: ID!): StockMove
    }
`)

const toStockMoveForGql = (id: MoveId, state: StockMove | undefined) => {
    if (state) {
        return { id, ...state }
    }
    return undefined
}

type MoveAction = (state: StockMove) => StockMoveResult

const doMoveAction = async (store: Store, rs: RestoredStockMove | undefined, 
    id: MoveId, action: MoveAction) => {

    if (rs) {
        const res = action(rs.state)

        if (res) {
            const [mv, ev] = res
            const info = StockMoveAction.info(mv)

            if (info) {
                const event = { 
                    move_id: id, 
                    revision: rs.revision + 1,
                    item: info.item,
                    from: info.from,
                    to: info.to,
                    event: ev
                }

                await store.saveEvent(event)
    
                return toStockMoveForGql(id, mv)
            }
        }
    }
    return undefined
}

const resolvers = {
    Stock: {
        __resolveType: (obj, ctx, info) => {
            if (obj.tag == 'stock.managed') {
                return 'ManagedStock'
            }
            return 'UnmanagedStock'
        }
    },
    StockMove: {
        __resolveType: (obj: StockMove, ctx, info) => {
            switch (obj.tag) {
                case 'stock-move.draft':
                    return 'DraftStockMove'
                case 'stock-move.completed':
                    return 'CompletedStockMove'
                case 'stock-move.cancelled':
                    return 'CancelledStockMove'
                case 'stock-move.assigned':
                    return 'AssignedStockMove'
                case 'stock-move.shipped':
                    return 'ShippedStockMove'
                case 'stock-move.arrived':
                    return 'ArrivedStockMove'
                case 'stock-move.assign-failed':
                    return 'AssignFailedStockMove'
                case 'stock-move.shipment-failed':
                    return 'ShipmentFailedStockMove'
            }
            return undefined
        }
    },
    Query: {
        findStock: async (parent, { item, location }, { store }, info) => {
            return store.loadStock(item, location)
        },
        findMove: async (parent, { id }, { store }, info) => {
            const res = await store.loadMove(id)
            return toStockMoveForGql(id, res?.state)
        }
    },
    Mutation: {
        createManaged: async (parent, { input: { item, location } }, { store }, info) => {
            const s = StockAction.newManaged(item, location)

            await store.saveStock(s)

            return s
        },
        createUnmanaged: async (parent, { input: { item, location } }, { store }, info) => {
            const s = StockAction.newUnmanaged(item, location)

            await store.saveStock(s)

            return s
        },
        start: async (parent, { input: { item, qty, from, to } }, { store }, info) => {
            const rs = { state: StockMoveAction.initialState(), revision: 0 }
            const id = `move-${uuidv4()}`

            return doMoveAction(
                store, rs, id, 
                s => StockMoveAction.start(s, item, qty, from, to)
            )
        },
        assign: async(parent, { id }, { store }, info) => {
            const rs = await store.loadMove(id)

            if (rs) {
                const info = StockMoveAction.info(rs.state)

                if (info) {
                    const stock = await store.loadStock(info.item, info.from)

                    return doMoveAction(
                        store, rs, id, 
                        s => StockMoveAction.assign(s, stock)
                    )
                }
            }
            return undefined
        },
        ship: async(parent, { id, outgoing }, { store }, info) => {
            const rs = await store.loadMove(id)

            return doMoveAction(
                store, rs, id, 
                s => StockMoveAction.ship(s, outgoing)
            )
        },
        arrive: async(parent, { id, incoming }, { store }, info) => {
            const rs = await store.loadMove(id)

            return doMoveAction(
                store, rs, id, 
                s => StockMoveAction.arrive(s, incoming)
            )
        },
        complete: async(parent, { id }, { store }, info) => {
            const rs = await store.loadMove(id)

            return doMoveAction(store, rs, id, StockMoveAction.complete)
        },
        cancel: async(parent, { id }, { store }, info) => {
            const rs = await store.loadMove(id)

            return doMoveAction(store, rs, id, StockMoveAction.cancel)
        }
    }
}

const run = async () => {
    const mongo = await MongoClient.connect(mongoUrl, { useUnifiedTopology: true })
    const eventsCol = mongo.db(dbName).collection(colName)
    const stocksCol = mongo.db(dbName).collection(stocksColName)

    const store = new Store(eventsCol, stocksCol)

    const server = new ApolloServer({
        typeDefs, 
        resolvers, 
        context: {
            store
        }
    })

    const res = await server.listen()

    console.log(res.url)
}

run().catch(err => console.error(err))
