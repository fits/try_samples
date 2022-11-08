
import { VoyageNumber } from '../common.ts'
import { Maybe } from '../utils/type_utils.ts'

import { gqlServe, error, postQuery, GraphQLDate } from '../utils/graphql_utils.ts'
import { MongoClient, ObjectId, Store } from '../utils/mongo_utils.ts'
import { connectNats, StreamEventBroker } from '../utils/nats_utils.ts'

import {
    TrackingId, Cargo, ActiveCargo, UnLocode, CargoAction,
    create, assignToRoute, changeDestination, changeDeadline, close, CargoEvent
} from './cargo.ts'

const port = parseInt(Deno.env.get('CARGO_PORT') ?? '8080')
const locationEndpoint = Deno.env.get('LOCATION_ENDPOINT') ?? 'http://localhost:8081'

const dbUrl = Deno.env.get('DB_ENDPOINT') ?? 'mongodb://127.0.0.1'
const dbName = Deno.env.get('DB_NAME') ?? 'cargo'
const colName = Deno.env.get('COLLECTION_NAME') ?? 'data'

const msgServer = Deno.env.get('MESSAGING_SERVER') ?? 'localhost'

type CargoStore = Store<ActiveCargo, CargoEvent>

type Context = {
    store: CargoStore,
    broker: StreamEventBroker<CargoEvent>
}

const schema = `
    scalar Date

    type RouteSpecification {
        origin: ID!
        destination: ID!
        arrivalDeadline: Date!
    }

    type LocationTime {
        location: ID!
        time: Date!
    }

    type Leg {
        voyageNo: ID!
        load: LocationTime!
        unload: LocationTime!
    }

    type Itinerary {
        legs: [Leg!]!
    }

    interface Cargo {
        trackingId: ID!
        routeSpec: RouteSpecification!
    }

    interface Routing {
        itinerary: Itinerary!
    }

    type UnroutedCargo implements Cargo {
        trackingId: ID!
        routeSpec: RouteSpecification!
    }

    type RoutedCargo implements Cargo & Routing {
        trackingId: ID!
        routeSpec: RouteSpecification!
        itinerary: Itinerary!
    }

    type MisroutedCargo implements Cargo & Routing {
        trackingId: ID!
        routeSpec: RouteSpecification!
        itinerary: Itinerary!
    }

    type ClosedCargo implements Cargo & Routing {
        trackingId: ID!
        routeSpec: RouteSpecification!
        itinerary: Itinerary!
    }

    input LegInput {
        voyageNo: ID!
        loadLocation: ID!
        loadTime: Date!
        unloadLocation: ID!
        unloadTime: Date!
    }

    type Query {
        find(trackingId: ID!): Cargo
    }

    type Mutation {
        create(origin: ID!, destination: ID!, deadline: Date!): Cargo
        assignToRoute(trackingId: ID!, legs: [LegInput!]!): Cargo
        close(trackingId: ID!): Cargo
        changeDestination(trackingId: ID!, destination: ID!): Cargo
        changeDeadline(trackingId: ID!, deadline: Date!): Cargo
    }
`

type TrackingIdInput = { trackingId: TrackingId }
type CreateInput = { origin: UnLocode, destination: UnLocode, deadline: Date }

type LegInput = { 
    voyageNo: VoyageNumber, 
    loadLocation: UnLocode, 
    loadTime: Date,
    unloadLocation: UnLocode,
    unloadTime: Date 
}

type AssignInput = {
    trackingId: TrackingId,
    legs: LegInput[]
}

type DestinationInput = {
    trackingId: TrackingId,
    destination: UnLocode
}

type DeadlineInput = {
    trackingId: TrackingId,
    deadline: Date
}

const doAction = async (ctx: Context, trackingId: TrackingId, action: CargoAction) => {
    const oid = new ObjectId(trackingId)
    let event: Maybe<CargoEvent>

    const r = await ctx.store.updateWithAction(oid, c => {
        const s = action(c)

        if (s) {
            event = s.event
            return { state: s.cargo, event: s.event }
        }

        return undefined
    })

    if (event) {
        await ctx.broker.publish(event.tag, event)
    }

    return r?.state
}

const existsLocation = async (location: UnLocode) => {
    const r = await postQuery(
        locationEndpoint, 
        `{
            find(unLocode: "${location}") {
                unLocode
            }
        }`
    )

    return r.data?.find?.unLocode === location
}

const validateLocations = async (locations: UnLocode[]) => {
    for (const loc of locations) {
        if (! await existsLocation(loc)) {
            throw error(`not found location: ${loc}`)
        }        
    }
}

const validateDeadline = (d: Date) => {
    if (d.getTime() <= Date.now()) {
        throw error(`non future date: ${d.toISOString()}`)
    }
}

const rootValue = {
    find: async ({ trackingId }: TrackingIdInput, { store }: Context ) => {
        const res = await store.find(new ObjectId(trackingId))
        return res?.state
    },
    create: async ({ origin, destination, deadline }: CreateInput, ctx: Context ) => {
        validateDeadline(deadline)
        await validateLocations([origin, destination])

        const [oid, rev] = await ctx.store.create()
        const trackingId = oid.toString()

        const s = create(trackingId, origin, destination, deadline)

        if (s) {
            const r = await ctx.store.update(oid, rev, s.cargo, s.event)

            await ctx.broker.publish(s.event.tag, s.event)

            return r?.state
        }

        return undefined
    },
    assignToRoute: async ({ trackingId, legs }: AssignInput, ctx: Context) => {
        await validateLocations(
            legs.reduce(
                (acc, loc) => acc.concat([loc.loadLocation, loc.unloadLocation]), 
                [] as UnLocode[]
            )
        )

        const itinerary = {
            legs: legs.map(leg => {
                return {
                    voyageNo: leg.voyageNo,
                    load: { location: leg.loadLocation, time: leg.loadTime },
                    unload: { location: leg.unloadLocation, time: leg.unloadTime }
                }
            })
        }

        return doAction(ctx, trackingId, assignToRoute(itinerary))
    },
    close: ({ trackingId }: TrackingIdInput, ctx: Context) => {
        return doAction(ctx, trackingId, close)
    },
    changeDestination: async ({ trackingId, destination }: DestinationInput, ctx: Context) => {
        await validateLocations([destination])

        return doAction(ctx, trackingId, changeDestination(destination))
    },
    changeDeadline: ({ trackingId, deadline }: DeadlineInput, ctx: Context) => {
        validateDeadline(deadline)
        
        return doAction(ctx, trackingId, changeDeadline(deadline))
    }
}

const mongo = new MongoClient()

await mongo.connect(dbUrl)
const db = mongo.database(dbName)

const nats = await connectNats({ servers: msgServer })

const contextValue: Context = {
    store: new Store(db.collection(colName)),
    broker: new StreamEventBroker(nats)
}

gqlServe({
    schema, 
    rootValue, 
    contextValue,
    typeResolvs: {
        Date: GraphQLDate,
        Cargo: {
            resolveType: (v: Cargo) => {
                switch (v.tag) {
                    case 'cargo.unrouted':
                        return 'UnroutedCargo'
                    case 'cargo.routed':
                        return 'RoutedCargo'
                    case 'cargo.misrouted':
                        return 'MisroutedCargo'
                    case 'cargo.closed':
                        return 'ClosedCargo'
                }
                return 'Unknown'
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
