
import { Maybe } from '../utils/type_utils.ts'

import { gqlServe, GraphQLDate, error } from '../utils/graphql_utils.ts'
import { MongoClient, ObjectId, Store } from '../utils/mongo_utils.ts'
import { connectNats, EventBroker } from '../utils/nats_utils.ts'

import {
    TrackingId, Delivery, UnLocode, VoyageNumber, DeliveryAction, HandlingEvent,
    create, receive, load, unload, claim, isMisdirected, isUnloadedAtDestination
} from './delivery.ts'

const port = parseInt(Deno.env.get('DELIVERY_PORT') ?? '8082')
const cargoEndpoint = Deno.env.get('CARGO_ENDPOINT') ?? 'http://localhost:8080'

const dbUrl = Deno.env.get('DB_ENDPOINT') ?? 'mongodb://127.0.0.1'
const dbName = Deno.env.get('DB_NAME') ?? 'delivery'
const colName = Deno.env.get('COLLECTION_NAME') ?? 'data'

const msgServer = Deno.env.get('MESSAGING_ENDPOINT') ?? 'localhost'

type DeliveryStore = Store<Delivery, HandlingEvent>

type Context = {
    store: DeliveryStore,
    broker: EventBroker<HandlingEvent>
}

const schema = `
    scalar Date

    interface Delivery {
        trackingId: ID!
    }

    interface Locating {
        location: ID!
    }

    interface OnBoarding {
        currentVoyageNo: ID!
    }

    interface Claiming {
        claimedTime: Date!
    }

    type NotReceivedDelivery implements Delivery {
        trackingId: ID!
    }

    type InPortDelivery implements Delivery & Locating {
        trackingId: ID!
        location: ID!
    }

    type OnBoardCarrierDelivery implements Delivery & Locating & OnBoarding {
        trackingId: ID!
        location: ID!
        currentVoyageNo: ID!
    }

    type ClaimedDelivery implements Delivery & Locating & Claiming {
        trackingId: ID!
        location: ID!
        claimedTime: Date!
    }

    type Query {
        find(trackingId: ID!): Delivery
        isMisdirected(trackingId: ID!): Boolean!
        isUnloadedAtDestination(trackingId: ID!): Boolean!
    }

    type Mutation {
        create(trackingId: ID!): Delivery
        receive(trackingId: ID!, location: ID!, completionTime: Date): Delivery
        load(trackingId: ID!, voyageNo: ID!, location: ID!, completionTime: Date): Delivery
        unload(trackingId: ID!, location: ID!, completionTime: Date): Delivery
        claim(trackingId: ID!, completionTime: Date): Delivery
    }
`

type TrackingIdInput = { trackingId: TrackingId }
type LoadInput = { trackingId: TrackingId, voyageNo: VoyageNumber, location: UnLocode, completionTime?: Date }
type UnloadInput = { trackingId: TrackingId, location: UnLocode, completionTime?: Date }
type ClaimInput = { trackingId: TrackingId, completionTime?: Date }

const doAction = async (ctx: Context, trackingId: TrackingId, action: DeliveryAction) => {
    const oid = new ObjectId(trackingId)
    let event: Maybe<HandlingEvent>

    const r = await ctx.store.updateWithAction(oid, c => {
        const s = action(c)

        if (s) {
            event = s.event
            return { state: s.delivery, event: s.event }
        }

        return undefined
    })

    if (event) {
        ctx.broker.publish(event.tag, event)
    }

    return r?.state
}

const findRouteAndSpec = async (trackingId: TrackingId) => {
    const r = await fetch(cargoEndpoint, {
        method: 'POST',
        body: `{
            find(trackingId: "${trackingId}") {
                trackingId
                routeSpec {
                    origin
                    destination
                    arrivalDeadline
                }
                ... on Routing {
                    itinerary {
                        legs {
                            voyageNo
                            load {
                                location
                                time
                            }
                            unload {
                                location
                                time
                            }
                        } 
                    }
                }
            }
        }`
    })

    const body = await r.json()

    if (body.data?.find) {
        return {
            itinerary: body.data.find.itinerary, 
            routeSpec: body.data.find.routeSpec
        }
    }

    return undefined
}

const findState = async (store: DeliveryStore, trackingId: TrackingId) => {
    const res = await store.find(new ObjectId(trackingId))
    return res?.state
}

const rootValue = {
    find: ({ trackingId }: TrackingIdInput, { store }: Context) => 
        findState(store, trackingId),
    isMisdirected: async ({ trackingId }: TrackingIdInput, { store }: Context) => {
        const s = await findState(store, trackingId)

        if (!s) {
            throw error(`not exists delivery: ${trackingId}`)
        }

        const r = await findRouteAndSpec(trackingId)

        return isMisdirected(s, (_tid) => r?.itinerary)
    },
    isUnloadedAtDestination: async ({ trackingId }: TrackingIdInput, { store }: Context) => {
        const s = await findState(store, trackingId)

        if (!s) {
            throw error(`not exists delivery: ${trackingId}`)
        }

        const r = await findRouteAndSpec(trackingId)

        return isUnloadedAtDestination(s, (_tid) => r ? [r.itinerary, r.routeSpec] : undefined)
    },
    create: async ({ trackingId }: TrackingIdInput, ctx: Context) => {
        const d = create(trackingId)

        if (d) {
            const oid = new ObjectId(trackingId)

            try {
                await ctx.store.create(oid, d)
            } catch(e) {
                console.warn(e)
                return undefined
            }

            const r = await ctx.store.find(oid)

            return r?.state
        }

        return undefined
    },
    receive: ({ trackingId, location, completionTime }: UnloadInput, ctx: Context) => {
        completionTime = completionTime ?? new Date()

        return doAction(ctx, trackingId, receive(trackingId, location, completionTime))
    },
    load: async ({ trackingId, voyageNo, location, completionTime }: LoadInput, ctx: Context) => {
        completionTime = completionTime ?? new Date()

        const r = await findRouteAndSpec(trackingId)

        return doAction(
            ctx,
            trackingId, 
            load(trackingId, voyageNo, location, completionTime, (_tid) => r?.itinerary)
        )
    },
    unload: async ({ trackingId, location, completionTime }: UnloadInput, ctx: Context) => {
        completionTime = completionTime ?? new Date()

        const r = await findRouteAndSpec(trackingId)

        return doAction(
            ctx,
            trackingId, 
            unload(trackingId, location, completionTime, (_tid) => r?.itinerary)
        )
    },
    claim: async ({ trackingId, completionTime }: ClaimInput, ctx: Context) => {
        completionTime = completionTime ?? new Date()

        const r = await findRouteAndSpec(trackingId)

        return doAction(
            ctx, 
            trackingId, 
            claim(trackingId, completionTime, (_tid) => r ? [r.itinerary, r.routeSpec] : undefined)
        )
    }
}

const mongo = new MongoClient()

await mongo.connect(dbUrl)
const db = mongo.database(dbName)

const nats = await connectNats({ servers: msgServer })

const contextValue: Context = {
    store: new Store(db.collection(colName)),
    broker: new EventBroker(nats)
}

gqlServe({
    schema, 
    rootValue, 
    contextValue,
    typeResolvs: {
        Date: GraphQLDate,
        Delivery: {
            resolveType: (v: Delivery) => {
                switch (v.tag) {
                    case 'delivery.not-received':
                        return 'NotReceivedDelivery'
                    case 'delivery.in-port':
                        return 'InPortDelivery'
                    case 'delivery.onboard-carrier':
                        return 'OnBoardCarrierDelivery'
                    case 'delivery.claimed':
                        return 'ClaimedDelivery'
                }
            }
        }
    },
    port,
    shutdownHook: async () => {
        mongo.close()

        await nats.flush()
        await nats.close()
    }
})

console.log(`listen: ${port}`)
