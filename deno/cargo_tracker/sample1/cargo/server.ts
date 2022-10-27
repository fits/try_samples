
import { VoyageNumber } from '../common.ts'
import { gqlServe, error, GraphQLDate } from '../utils/graphql_utils.ts'

import {
    TrackingId, Cargo, ActiveCargo, UnLocode, CargoAction,
    create, assignToRoute, changeDestination, changeDeadline, close
} from './cargo.ts'

const port = parseInt(Deno.env.get('CARGO_PORT') ?? '8080')
const locationEndpoint = Deno.env.get('LOCATION_ENDPOINT') ?? 'http://localhost:8081'

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

const store: { [key: TrackingId]: ActiveCargo } = {}

const doAction = (trackingId: TrackingId, action: CargoAction) => {
    const c = store[trackingId]

    if (c) {
        const r = action(c)

        if (r) {
            store[trackingId] = r.cargo
            return r.cargo
        }
    }

    return undefined
}

const existsLocation = async (location: UnLocode) => {
    const r = await fetch(locationEndpoint, {
        method: 'POST',
        body: `{
            find(unLocode: "${location}") {
                unLocode
            }
        }`
    })

    const body = await r.json()

    return body.data?.find?.unLocode === location
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

const createTrackingId = () => {
    for (let i = 0; i < 10; i++) {
        const id = `tid-${crypto.randomUUID()}`

        if (!store[id]) {
            return id
        }
    }

    throw error('failed create trackingId')
}

const rootValue = {
    find: ({ trackingId }: TrackingIdInput) => store[trackingId],
    create: async ({ origin, destination, deadline }: CreateInput ) => {
        validateDeadline(deadline)
        await validateLocations([origin, destination])

        const r = create(createTrackingId(), origin, destination, deadline)

        if (r) {
            store[r.cargo.trackingId] = r.cargo
        }

        return r?.cargo
    },
    assignToRoute: async ({ trackingId, legs }: AssignInput) => {
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

        return doAction(trackingId, assignToRoute(itinerary))
    },
    close: ({ trackingId }: TrackingIdInput) => {
        return doAction(trackingId, close)
    },
    changeDestination: async ({ trackingId, destination }: DestinationInput) => {
        await validateLocations([destination])

        return doAction(trackingId, changeDestination(destination))
    },
    changeDeadline: ({ trackingId, deadline }: DeadlineInput) => {
        validateDeadline(deadline)
        
        return doAction(trackingId, changeDeadline(deadline))
    }
}

gqlServe({
    schema, 
    rootValue, 
    port, 
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
    }
})

console.log(`listen: ${port}`)
