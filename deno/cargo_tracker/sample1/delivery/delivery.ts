
import { UnLocode, VoyageNumber, RouteSpecification, Itinerary, Leg } from '../common.ts'

export type { UnLocode, VoyageNumber } from '../common.ts'

export type TrackingId = string

export type HandlingEvent = Received | Loaded | Unloaded | Claimed

export interface Received {
    tag: 'transport-event.received'
    trackingId: TrackingId
    location: UnLocode
    completionTime: Date
}

export interface Loaded {
    tag: 'transport-event.loaded'
    trackingId: TrackingId
    voyageNo: VoyageNumber
    location: UnLocode
    completionTime: Date
}

export interface Unloaded {
    tag: 'transport-event.unloaded'
    trackingId: TrackingId
    voyageNo: VoyageNumber
    location: UnLocode
    completionTime: Date
}

export interface Claimed {
    tag: 'transport-event.claimed'
    trackingId: TrackingId
    completionTime: Date
}

export type Delivery = NotReceivedDelivery | 
    InPortDelivery | OnBoardCarrierDelivery | ClaimedDelivery

export interface NotReceivedDelivery {
    tag: 'delivery.not-received'
    trackingId: TrackingId
}

export interface InPortDelivery {
    tag: 'delivery.in-port'
    trackingId: TrackingId
    location: UnLocode
}

export interface OnBoardCarrierDelivery {
    tag: 'delivery.onboard-carrier'
    trackingId: TrackingId
    currentVoyageNo: VoyageNumber
    location: UnLocode
}

export interface ClaimedDelivery {
    tag: 'delivery.claimed'
    trackingId: TrackingId
    location: UnLocode
    claimedTime: Date
}

type DeliveryResult = { delivery: Delivery, event: HandlingEvent } | undefined
export type DeliveryAction = (state: Delivery) => DeliveryResult
type DeliveryOptional = Delivery | undefined

export type FindRoute = (trackingId: TrackingId) => Itinerary | undefined
export type FindRouteAndSpec = (trackingId: TrackingId) => [Itinerary, RouteSpecification] | undefined

export function create(trackingId: TrackingId): DeliveryOptional {
    if (trackingId.trim().length == 0) {
        return undefined
    }

    return {
        tag: 'delivery.not-received',
        trackingId
    }
}

export function receive(trackingId: TrackingId, location: UnLocode, 
    completionTime: Date): DeliveryAction {

    return state => {
        const event: Received = {
            tag: 'transport-event.received',
            trackingId,
            location,
            completionTime
        }

        return applyEvent(state, event)
    }
}

export function load(trackingId: TrackingId, voyageNo: VoyageNumber, location: UnLocode, 
    completionTime: Date, findRoute: FindRoute): DeliveryAction {

    return state => {
        const it = findRoute(trackingId)

        if (!it || notFoundVoyage(it, voyageNo)) {
            return undefined
        }

        const event: Loaded = {
            tag: 'transport-event.loaded',
            trackingId,
            voyageNo,
            location,
            completionTime
        }

        return applyEvent(state, event)
    }
}

export function unload(trackingId: TrackingId, location: UnLocode, 
    completionTime: Date, findRoute: FindRoute): DeliveryAction {

    return state => {
        const it = findRoute(trackingId)
        const voyageNo = (state.tag == 'delivery.onboard-carrier') ? state.currentVoyageNo : ''

        if (!it || notFoundVoyage(it, voyageNo)) {
            return undefined
        }
    
        const event: HandlingEvent = {
            tag: 'transport-event.unloaded',
            trackingId,
            voyageNo,
            location,
            completionTime
        } 

        return applyEvent(state, event)
    }
}

export function claim(trackingId: TrackingId, completionTime: Date, 
    findRoute: FindRouteAndSpec): DeliveryAction {

    return state => {
        if (isUnloadedAtDestination(state, findRoute)) {
            const event: Claimed = {
                tag: 'transport-event.claimed',
                trackingId,
                completionTime
            }

            return applyEvent(state, event)
        }

        return undefined
    }
}

export function isUnloadedAtDestination(state: Delivery, findRoute: FindRouteAndSpec): boolean {
    const r = findRoute(state.trackingId)

    if (!r || r[0].legs.length == 0) {
        return false
    }

    return state.tag == 'delivery.in-port' && isArrivedLocation(state.location, r[0], r[1])
}

export function isMisdirected(state: Delivery, findRoute: FindRoute): boolean {
    const it = findRoute(state.trackingId)

    if (it && it.legs.length > 0) {
        switch (state.tag) {
            case 'delivery.in-port':
                return isMisdirectedForInport(state, it.legs)
            case 'delivery.onboard-carrier':
                return isMisdirectedForOnBoardCarrier(state, it.legs)
            case 'delivery.claimed':
                return isMisdirectedForClaimed(state, it.legs)
        }
    }

    return false
}

function isMisdirectedForInport(state: InPortDelivery, legs: Leg[]): boolean {
    const leg = legs.find(l =>
        [ l.load.location, l.unload.location ].includes(state.location)
    )
    
    return leg == undefined
}

function isMisdirectedForOnBoardCarrier(state: OnBoardCarrierDelivery, legs: Leg[]): boolean {
    const leg = legs.find(l =>
        l.voyageNo == state.currentVoyageNo && l.load.location == state.location
    )
    
    return leg == undefined
}

function isMisdirectedForClaimed(state: ClaimedDelivery, legs: Leg[]): boolean {
    return lastLocation(legs) != state.location
}

function isArrivedLocation(location: UnLocode, itinerary: Itinerary, 
    routeSpec: RouteSpecification): boolean {

    return [ lastLocation(itinerary.legs), routeSpec.destination ].includes(location)
}

function lastLocation(legs: Leg[]): UnLocode {
    return legs[legs.length - 1].unload.location
}

function notFoundVoyage(itinerary: Itinerary, voyageNo: VoyageNumber): boolean {
    return itinerary.legs.find(l => l.voyageNo == voyageNo) == undefined
}

function applyEvent(state: Delivery, event: HandlingEvent): DeliveryResult {
    const delivery = transit(state, event)

    if (delivery) {
        return { delivery, event }
    }

    return undefined
}

function transit(state: Delivery, event: HandlingEvent): DeliveryOptional {
    if (state.trackingId != event.trackingId) {
        return undefined
    }

    switch (state.tag) {
        case 'delivery.not-received':
            if (event.tag == 'transport-event.received') {
                return {
                    tag: 'delivery.in-port', 
                    trackingId: state.trackingId,
                    location: event.location
                }
            }
            break
        case 'delivery.in-port':
            switch (event.tag) {
                case 'transport-event.loaded':
                    return {
                        tag: 'delivery.onboard-carrier',
                        trackingId: state.trackingId,
                        currentVoyageNo: event.voyageNo,
                        location: event.location
                    }
                case 'transport-event.claimed':
                    return {
                        tag: 'delivery.claimed',
                        trackingId: state.trackingId,
                        location: state.location,
                        claimedTime: event.completionTime
                    }                    
            }
            break
        case 'delivery.onboard-carrier':
            switch (event.tag) {
                case 'transport-event.unloaded':
                    return {
                        tag: 'delivery.in-port',
                        trackingId: state.trackingId,
                        location: event.location
                    }
            }
            break
    }

    return undefined
}
