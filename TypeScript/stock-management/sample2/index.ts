
import { ApolloServer, gql } from 'apollo-server'
import { v4 as uuidv4 } from 'uuid'

import {
    StockMoveAction, StockMoveRestore, StockMove, StockMoveEvent, 
    StockAction, StockRestore, Stock
} from './models'

interface StoredEvent {
    id: string
    event: StockMoveEvent
}

class Store {
    private data = { 
        stocks: {}, 
        events: [] as StoredEvent[]
    }

    async existStock(item: string, location: string): Promise<boolean> {
        const id = this.stockId(item, location)

        return id in this.data.stocks
    }

    async loadStock(item: string, location: string): Promise<Stock | undefined> {
        const id = this.stockId(item, location)
        const stock = this.data.stocks[id]

        if (stock) {
            const events = this.data.events.map(e => e.event)
            return StockRestore.restore(stock, events)
        }

        return undefined
    }

    async saveStock(stock: Stock): Promise<void> {
        const id = this.stockId(stock.item, stock.location)
        this.data.stocks[id] = stock
    }

    async loadMove(id: string): Promise<StockMove | undefined> {
        const events = this.data.events.filter(e => e.id == id).map(e => e.event)
        const state = StockMoveAction.initial()

        const res = StockMoveRestore.restore(state, events)

        return (res == state) ? undefined : res
    }

    async saveEvent(event: StoredEvent): Promise<void> {
        this.data.events.push(event)
    }

    private stockId(item: string, location: string): string {
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

const toStockMoveForGql = (id, state) => {
    if (state) {
        return { id, ...state }
    }
    return undefined
}

const doMoveAction = async (store, state, id, action) => {
    if (state) {
        const res = action(state)

        if (res) {
            const event = { id, event: res[1] }
            await store.saveEvent(event)

            return toStockMoveForGql(id, res[0])
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
            const state = await store.loadMove(id)
            return toStockMoveForGql(id, state)
        }
    },
    Mutation: {
        createManaged: async (parent, { input: { item, location } }, { store }, info) => {
            if (await store.existStock(item, location)) {
                return undefined
            }

            const s = StockAction.newManaged(item, location)

            await store.saveStock(s)

            return s
        },
        createUnmanaged: async (parent, { input: { item, location } }, { store }, info) => {
            if (await store.existStock(item, location)) {
                return undefined
            }

            const s = StockAction.newUnmanaged(item, location)

            await store.saveStock(s)

            return s
        },
        start: async (parent, { input: { item, qty, from, to } }, { store }, info) => {
            const state = StockMoveAction.initial()
            const id = `move-${uuidv4()}`

            return doMoveAction(
                store, state, id, 
                s => StockMoveAction.start(s, item, qty, from, to)
            )
        },
        assign: async(parent, { id }, { store }, info) => {
            const state = await store.loadMove(id)
            const moveInfo = StockMoveAction.info(state)

            if (moveInfo) {
                const stock = await store.loadStock(moveInfo.item, moveInfo.from)

                return doMoveAction(
                    store, state, id, 
                    s => StockMoveAction.assign(s, (item, loc) => stock)
                )
            }
            return undefined
        },
        ship: async(parent, { id, outgoing }, { store }, info) => {
            const state = await store.loadMove(id)

            return doMoveAction(
                store, state, id, 
                s => StockMoveAction.ship(s, outgoing)
            )
        },
        arrive: async(parent, { id, incoming }, { store }, info) => {
            const state = await store.loadMove(id)

            return doMoveAction(
                store, state, id, 
                s => StockMoveAction.arrive(s, incoming)
            )
        },
        complete: async(parent, { id }, { store }, info) => {
            const state = await store.loadMove(id)

            return doMoveAction(store, state, id, StockMoveAction.complete)
        },
        cancel: async(parent, { id }, { store }, info) => {
            const state = await store.loadMove(id)

            return doMoveAction(store, state, id, StockMoveAction.cancel)
        }
    }
}

const run = async () => {
    const store = new Store()

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
