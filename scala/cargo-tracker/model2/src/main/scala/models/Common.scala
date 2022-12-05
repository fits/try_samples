package models

import java.time.LocalDateTime

type UnLocode = String
type VoyageNo = String
type Date = LocalDateTime

case class RouteSpecification(origin: UnLocode, destination: UnLocode, deadline: Date)

case class Itinerary(legs: List[Leg])

case class LocationTime(location: UnLocode, time: Date)

case class Leg(voyageNo: VoyageNo, load: LocationTime, unload: LocationTime)
