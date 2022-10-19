
import { UnLocode, RouteSpecification, Itinerary, LocationTime } from '../common.ts'

export type { UnLocode, RouteSpecification, Itinerary, LocationTime } from '../common.ts'

export type TrackingId = string

export type CargoEvent = CargoCreated | DestinationChanged | DeadlineChanged | 
    RouteAssigned | Closed

export interface CargoCreated {
    tag: 'cargo-event.created'
    trackingId: TrackingId
    routeSpec: RouteSpecification
}

export interface DestinationChanged {
    tag: 'cargo-event.destination-changed'
    trackingId: TrackingId
    newDestination: UnLocode
}

export interface DeadlineChanged {
    tag: 'cargo-event.deadline-changed'
    trackingId: TrackingId
    newDeadline: Date
}

export interface RouteAssigned {
    tag: 'cargo-event.route-assigned'
    trackingId: TrackingId
    itinerary: Itinerary
}

export interface Closed {
    tag: 'cargo-event.closed'
    trackingId: TrackingId
}

export type Cargo = NoneCargo | ActiveCargo
export type ActiveCargo = UnroutedCargo | RoutedCargo | MisroutedCargo | ClosedCargo

export interface NoneCargo {
    tag: 'cargo.none'
}

export interface UnroutedCargo {
    tag: 'cargo.unrouted'
    trackingId: TrackingId
    routeSpec: RouteSpecification
}

export interface RoutedCargo {
    tag: 'cargo.routed'
    trackingId: TrackingId
    routeSpec: RouteSpecification
    itinerary: Itinerary
}

export interface MisroutedCargo {
    tag: 'cargo.misrouted'
    trackingId: TrackingId
    routeSpec: RouteSpecification
    itinerary: Itinerary
}

export interface ClosedCargo {
    tag: 'cargo.closed'
    trackingId: TrackingId
    routeSpec: RouteSpecification
    itinerary: Itinerary
}

export type CargoAction = (state: ActiveCargo) => CargoResult
type CargoResult = { cargo: ActiveCargo, event: CargoEvent } | undefined
type CargoOptional = ActiveCargo | undefined

const noneCargo: NoneCargo = { tag: 'cargo.none' }

export function create(trackingId: TrackingId, origin: UnLocode, destination: UnLocode, 
    arrivalDeadline: Date): CargoResult {

    if (!isFuture(arrivalDeadline)) {
        return undefined
    }

    const routeSpec = { origin, destination, arrivalDeadline }

    return applyEvent(noneCargo, {
        tag: 'cargo-event.created',
        trackingId,
        routeSpec
    })
}

export function changeDestination(destination: UnLocode): CargoAction {
    return state => {
        if (state.routeSpec.destination == destination) {
            return undefined
        }

        return applyEvent(state, {
            tag: 'cargo-event.destination-changed',
            trackingId: state.trackingId,
            newDestination: destination
        })
    }
}

export function changeDeadline(deadline: Date): CargoAction {
    return state => {
        if (!isFuture(deadline)) {
            return undefined
        }

        if (state.routeSpec.arrivalDeadline.getTime() == deadline.getTime()) {
            return undefined
        }

        return applyEvent(state, {
            tag: 'cargo-event.deadline-changed',
            trackingId: state.trackingId,
            newDeadline: deadline
        })
    }
}

export function assignToRoute(itinerary: Itinerary): CargoAction {
    return state => {
        return applyEvent(state, {
            tag: 'cargo-event.route-assigned',
            trackingId: state.trackingId,
            itinerary
        })
    }
}

export function close(state: ActiveCargo): CargoResult {
    return applyEvent(state, {
        tag: 'cargo-event.closed',
        trackingId: state.trackingId
    })
}

function applyEvent(state: Cargo, event: CargoEvent): CargoResult {
    const cargo = transit(state, event)

    if (cargo) {
        return { cargo, event }
    }

    return undefined
}

function transit(state: Cargo, event: CargoEvent): CargoOptional {
    switch (state.tag) {
        case 'cargo.closed':
            return undefined
        case 'cargo.none':
            if (event.tag != 'cargo-event.created') {
                return undefined
            }

            return {
                tag: 'cargo.unrouted',
                trackingId: event.trackingId,
                routeSpec: event.routeSpec
            }
        case 'cargo.unrouted':
            if (state.trackingId != event.trackingId) {
                return undefined
            }
            return transitForUnrouted(state, event)
        case 'cargo.routed':
            if (state.trackingId != event.trackingId) {
                return undefined
            }
            return transitForRouted(state, event)
        case 'cargo.misrouted':
            if (state.trackingId != event.trackingId) {
                return undefined
            }
            return transitForRouted(state, event)
    }
}

function transitForUnrouted(state: UnroutedCargo, event: CargoEvent): CargoOptional {
    switch (event.tag) {
        case 'cargo-event.created':
        case 'cargo-event.closed':
            return undefined
        case 'cargo-event.destination-changed':
            return {
                ...state,
                routeSpec: { ...state.routeSpec, destination: event.newDestination }
            }            
        case 'cargo-event.deadline-changed':
            return {
                ...state,
                routeSpec: { ...state.routeSpec, arrivalDeadline: event.newDeadline }
            }
        case 'cargo-event.route-assigned':
            return inspectRoute({
                ...state,
                tag: 'cargo.routed',
                itinerary: event.itinerary
            })
    }
}

function transitForRouted(state: RoutedCargo | MisroutedCargo, event: CargoEvent): CargoOptional {
    switch (event.tag) {
        case 'cargo-event.created':
            return undefined
        case 'cargo-event.closed':
            return { ...state, tag: 'cargo.closed' }
        case 'cargo-event.destination-changed':
            return inspectRoute({
                ...state, 
                routeSpec: { ...state.routeSpec, destination: event.newDestination }
            })
        case 'cargo-event.deadline-changed':
            return inspectRoute({ 
                ...state, 
                routeSpec: { ...state.routeSpec, arrivalDeadline: event.newDeadline } 
            })
        case 'cargo-event.route-assigned':
            return inspectRoute({ 
                ...state,
                itinerary: event.itinerary
            })
    }
}

function inspectRoute(state: RoutedCargo | MisroutedCargo): ActiveCargo {
    const ok = isSatisfiedWithRoute(state.itinerary, state.routeSpec)

    if (ok && state.tag == 'cargo.misrouted') {
        return { ...state, tag: 'cargo.routed' }
    }

    if (!ok && state.tag == 'cargo.routed') {
        return { ...state, tag: 'cargo.misrouted'}
    }

    return state
}

const isFuture = (date: Date) => date.getTime() > Date.now()

const isSatisfiedWithRoute = (itinerary: Itinerary, routeSpec: RouteSpecification) => {
    const r = itinerary.legs.reduce((acc, v) => {
        const { location, time } = v.load

        if (acc && location == acc.location && time.getTime() >= acc.time.getTime()) {
            return v.unload
        }

        return undefined
    }, { location: routeSpec.origin, time: new Date(0) } as LocationTime | undefined)

    return r && r.location == routeSpec.destination && 
        r.time.getTime() < routeSpec.arrivalDeadline.getTime()
}
