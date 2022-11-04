
export type UnLocode = string
export type VoyageNumber = string

export interface RouteSpecification {
    origin: UnLocode
    destination: UnLocode
    arrivalDeadline: Date
}

export interface Itinerary {
    legs: Leg[]
}

export interface LocationTime {
    location: UnLocode
    time: Date
}

export interface Leg {
    voyageNo: VoyageNumber
    load: LocationTime
    unload: LocationTime
}
