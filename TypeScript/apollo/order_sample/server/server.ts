
import { ApolloServer, gql } from 'apollo-server'
import { v4 as uuidv4 } from 'uuid'
import { MongoClient, Collection } from 'mongodb'

import {
    Order, OrderEvent, BillingLine, 
    OrderAction, OrderUtil, OrderRestore
} from './models'

const DELIVERY_FEE = 550

const SHOP = 'shop-1'
const mongoUrl = 'mongodb://localhost'
const dbName = 'order'
const colName = 'data'
const colItemsName = 'items'

type UserId = string
type ShopId = string
type PaymentId = string
type DeliveryId = string

type BasicOrder = Order<UserId, ShopId, PaymentId, DeliveryId>
type BasicOrderEvent = OrderEvent<UserId, ShopId, PaymentId, DeliveryId>

type FindPrice = (item: string) => Promise<number | undefined>

interface OrderItem {
    item: string
    price: number
    qty: number
}

interface OrderState {
    id: string
    items: [OrderItem]
    payment: { paymentId: string, fee: number }
    delivery: { deliveryId: string, fee: number }
    billing: { total: number }
}

interface StoredState {
    id: string
    state: OrderState
    originState: BasicOrder
    events: [BasicOrderEvent]
}

type NullableStoredState = StoredState | null | undefined
type NullableOrderState = OrderState | null | undefined

interface Store {
    load(id: string): Promise<NullableStoredState>
    save(id: string, state: BasicOrder, event: BasicOrderEvent): Promise<NullableOrderState>
}

class MongoStore implements Store {
    private col: Collection<StoredState>

    constructor(col: Collection<StoredState>) {
        this.col = col
    }

    async load(id: string): Promise<NullableStoredState> {
        return this.col.findOne({ _id: id })
    }

    async save(id: string, state: BasicOrder, event: BasicOrderEvent): Promise<NullableOrderState> {
        const newState = OrderRestore.restore(state, [event])

        if (newState && newState.tag != 'none-order') {
            const payment = newState.payment ?
                { paymentId: newState.payment.method, fee: newState.payment.fee } : undefined

            const delivery = newState.delivery ?
                { deliveryId: newState.delivery.method, fee: newState.delivery.fee } : undefined
    
            const billing = newState.billing ?
                { total: newState.billing.total } : undefined
    
            const res = { 
                id, 
                items: newState.items, 
                payment, 
                delivery, 
                billing
            } as OrderState

            const data = await this.load(id)
            data?.events.push(event)

            const rec: StoredState = {
                id: id,
                state: res,
                originState: newState,
                events: (data) ? data.events : [event]
            }

            const upd = await this.col.updateOne(
                { _id: id },
                { $set: rec },
                { upsert: true }
            )

            if (upd.upsertedCount > 0 || upd.modifiedCount > 0) {
                return res
            }
        }
        return undefined
    }
}


const typeDefs = gql(`
    type OrderItem {
        item: String!
        price: Int!
        qty: Int!
    }

    type Payment {
        fee: Int!
    }

    type Delivery {
        fee: Int!
    }

    type Billing {
        total: Int!
    }

    type Order {
        id: ID!
        items: [OrderItem!]!
        payment: Payment
        delivery: Delivery
        billing: Billing
    }

    type Query {
        find(id: ID!): Order
    }

    input StartRequest {
        user: String!
    }

    input ItemRequest {
        id: ID!
        item: String!
        qty: Int!
    }

    input PaymentRequest {
        id: ID!
        paymentId: ID!
    }

    input DeliveryRequest {
        id: ID!
        deliveryId: String!
    }

    input CalculateRequest {
        id: ID!
    }

    input ConfirmRequest {
        id: ID!
    }

    type Mutation {
        start(input: StartRequest!): Order
        changeItem(input: ItemRequest!): Order
        selectPayment(input: PaymentRequest!): Order
        selectDelivery(input: DeliveryRequest!): Order
        calculate(input: CalculateRequest!): Order
        confirm(input: ConfirmRequest!): Order
    }
`)

const resolvers = {
    Query: {
        find: async (_parent, { id }, { store }) => {
            const r = await store.load(id)
            return r?.state
        }
    },
    Mutation: {
        start: async (_parent, { input: { user } }, { store }) => {
            const id = `order-${uuidv4()}`

            const state = OrderUtil.none()

            const event: BasicOrderEvent = 
                OrderAction.start(state, user, SHOP)

            if (event) {
                return store.save(id, state, event)
            }

            return undefined
        },
        changeItem: async (_parent, { input: { id, item, qty } }, { store, findPrice }) => {
            const state = (await store.load(id))?.originState

            if (state) {
                const itemPrice = await findPrice(item)

                const event = OrderAction.changeItem(
                    state, item, qty, (_) => itemPrice
                )

                if (event) {
                    return store.save(id, state, event)
                }
            }

            return undefined
        },
        selectPayment: async (_parent, { input: { id, paymentId } }, { store }) => {
            const state = (await store.load(id))?.originState

            if (state) {
                const event = OrderAction.selectPayment(state, paymentId, 0)

                if (event) {
                    return store.save(id, state, event)
                }
            }

            return undefined
        },
        selectDelivery: async (_parent, { input: { id, deliveryId } }, { store }) => {
            const state = (await store.load(id))?.originState

            if (state) {
                const event = OrderAction.selectDelivery(
                    state, 
                    deliveryId, 
                    DELIVERY_FEE
                )

                if (event) {
                    return store.save(id, state, event)
                }
            }

            return undefined
        },
        calculate: async (_parent, { input: { id } }, { store }) => {
            const state = (await store.load(id))?.originState

            if (state && state.tag != 'none-order') {
                const lines = state.items.map(i => 
                    <BillingLine>({
                        name: i.item,
                        unitAmount: i.price,
                        qty: i.qty,
                        subTotal: i.price * i.qty
                    })
                )

                if (state.delivery?.fee > 0) {
                    lines.push({
                        name: 'delivery fee',
                        unitAmount: state.delivery.fee,
                        qty: 1,
                        subTotal: state.delivery.fee
                    })
                }

                if (state.payment?.fee > 0) {
                    lines.push({
                        name: 'payment fee',
                        unitAmount: state.payment.fee,
                        qty: 1,
                        subTotal: state.payment.fee
                    })
                }

                const total = lines.reduce(
                    (acc, line) => acc + line.subTotal, 
                    0
                )

                const event = OrderAction.confirmBilling(
                    state, 
                    { total, lines }
                )

                if (event) {
                    return store.save(id, state, event)
                }
            }

            return undefined
        },
        confirm: async (_parent, { input: { id } }, { store }) => {
            const state = (await store.load(id))?.originState

            if (state) {
                const event = OrderAction.confirmOrder(state)

                if (event) {
                    return store.save(id, state, event)
                }
            }

            return undefined
        }
    }
}

const run = async () => {
    const client = await MongoClient.connect(mongoUrl)

    const col: Collection<StoredState> = client.db(dbName).collection(colName)
    const store = new MongoStore(col)

    const findPrice: FindPrice = async (item: string) => {
        const r = await client.db(dbName).collection(colItemsName).findOne({
            _id: item
        })

        return r?.price
    }

    const server = new ApolloServer({
        typeDefs, 
        resolvers, 
        context: {
            store,
            findPrice
        }
    })

    const res = await server.listen()

    console.log(`started: ${res.url}`)
}

run().catch(err => console.error(err))
