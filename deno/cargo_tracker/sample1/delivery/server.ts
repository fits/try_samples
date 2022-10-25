
import { gqlServe, GraphQLDate, error } from '../utils/graphql_utils.ts'

import {
    TrackingId, Delivery, UnLocode, VoyageNumber, DeliveryAction,
    create, receive, load, unload, claim, isMisdirected, isUnloadedAtDestination
} from './delivery.ts'

const port = parseInt(Deno.env.get('DELIVERY_PORT') ?? '8082')
const cargoEndpoint = Deno.env.get('CARGO_ENDPOINT') ?? 'http://localhost:8080'

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

const store: { [key: TrackingId]: Delivery } = {}

const doAction = (trackingId: TrackingId, action: DeliveryAction) => {
    const c = store[trackingId]

    if (c) {
        const r = action(c)

        if (r) {
            store[trackingId] = r.delivery
            return r.delivery
        }
    }

    return undefined
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

const rootValue = {
    find: ({ trackingId }: TrackingIdInput) => store[trackingId],
    isMisdirected: async ({ trackingId }: TrackingIdInput) => {
        const s = store[trackingId]

        if (!s) {
            throw error(`not exists delivery: ${trackingId}`)
        }

        const r = await findRouteAndSpec(trackingId)

        return isMisdirected(s, (_tid) => r?.itinerary)
    },
    isUnloadedAtDestination: async ({ trackingId }: TrackingIdInput) => {
        const s = store[trackingId]

        if (!s) {
            throw error(`not exists delivery: ${trackingId}`)
        }

        const r = await findRouteAndSpec(trackingId)

        return isUnloadedAtDestination(s, (_tid) => r ? [r.itinerary, r.routeSpec] : undefined)
    },
    create: ({ trackingId }: TrackingIdInput) => {
        if (!store[trackingId]) {
            const d = create(trackingId)

            if (d) {
                store[trackingId] = d
                return d
            }
        }
        return undefined
    },
    receive: ({ trackingId, location, completionTime }: UnloadInput) => {
        completionTime = completionTime ?? new Date()

        return doAction(trackingId, receive(trackingId, location, completionTime))
    },
    load: async ({ trackingId, voyageNo, location, completionTime }: LoadInput) => {
        completionTime = completionTime ?? new Date()

        const r = await findRouteAndSpec(trackingId)

        return doAction(
            trackingId, 
            load(trackingId, voyageNo, location, completionTime, (_tid) => r?.itinerary)
        )
    },
    unload: async ({ trackingId, location, completionTime }: UnloadInput) => {
        completionTime = completionTime ?? new Date()

        const r = await findRouteAndSpec(trackingId)

        return doAction(
            trackingId, 
            unload(trackingId, location, completionTime, (_tid) => r?.itinerary)
        )
    },
    claim: async ({ trackingId, completionTime }: ClaimInput) => {
        completionTime = completionTime ?? new Date()

        const r = await findRouteAndSpec(trackingId)

        return doAction(trackingId, claim(trackingId, completionTime, (_tid) => 
            r ? [r.itinerary, r.routeSpec] : undefined
        ))
    }
}

gqlServe({
    schema, 
    rootValue, 
    port, 
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
    }
})

console.log(`listen: ${port}`)
