package cargo

import cats.{Id, ~>}
import cats.arrow.FunctionK
import cats.free.Free
import Free.liftF
import cats.data.State

import java.time.LocalDateTime

type TrackingId = String
type UnLocode = String
type VoyageNo = String
type Date = LocalDateTime

case class RouteSpecification(origin: UnLocode, destination: UnLocode, deadline: Date)

case class Itinerary(legs: List[Leg])

case class LocationTime(location: UnLocode, time: Date)

case class Leg(voyageNo: VoyageNo, load: LocationTime, unload: LocationTime)

trait HasRouteSpec {
  val trackingId: TrackingId
  val routeSpec: RouteSpecification
}

trait HasItinerary extends HasRouteSpec {
  val itinerary: Itinerary
}

enum Cargo:
  case Empty()
  case Unrouted(trackingId: TrackingId, routeSpec: RouteSpecification) extends Cargo, HasRouteSpec
  case Routed(trackingId: TrackingId, routeSpec: RouteSpecification, itinerary: Itinerary) extends Cargo, HasItinerary
  case Misrouted(trackingId: TrackingId, routeSpec: RouteSpecification, itinerary: Itinerary) extends Cargo, HasItinerary
  case Closed(trackingId: TrackingId, routeSpec: RouteSpecification, itinerary: Itinerary) extends Cargo, HasItinerary

enum Event:
  case Created(trackingId: TrackingId, routeSpec: RouteSpecification)
  case AssignedRoute(trackingId: TrackingId, itinerary: Itinerary)
  case ChangedDestination(trackingId: TrackingId, newDestination: UnLocode)
  case ChangedDeadline(trackingId: TrackingId, newDeadline: Date)
  case Closed(trackingId: TrackingId)

enum CommandA[A]:
  case Create(trackingId: TrackingId, routeSpec: RouteSpecification) extends CommandA[List[Event]]
  case AssignRoute(itinerary: Itinerary) extends CommandA[List[Event]]
  case ChangeDestination(newDestination: UnLocode) extends CommandA[List[Event]]
  case ChangeDeadline(newDeadline: Date) extends CommandA[List[Event]]
  case Close() extends CommandA[List[Event]]

type Command[A] = Free[CommandA, A]

import CommandA.*

type CargoState[A] = State[Cargo, A]

object CargoAction:
  def emptyCargo = Cargo.Empty()

  def create(trackingId: TrackingId, origin: UnLocode, destination: UnLocode, deadline: Date) =
    liftF(Create(trackingId, RouteSpecification(origin, destination, deadline)))

  def assignRoute(itinerary: Itinerary) = liftF(AssignRoute(itinerary))

  def changeDestination(destination: UnLocode) = liftF(ChangeDestination(destination))

  def changeDeadline(deadline: Date) = liftF(ChangeDeadline(deadline))

  def close() = liftF(Close())

  def interpret: CommandA ~> CargoState =
    new (CommandA ~> CargoState) {
      def apply[A](cmd: CommandA[A]): CargoState[A] =
        cmd match {
          case Create(t, r) => State {
            case Cargo.Empty() if t.nonBlank && r.deadline.isFuture =>
              (Cargo.Unrouted(t, r), List(Event.Created(t, r)))
            case s => (s, List.empty[Event])
          }
          case AssignRoute(i) => State {
            case s: (Cargo.Unrouted | Cargo.Routed | Cargo.Misrouted) if i.legs.nonEmpty =>
              (
                inspectRoute(Cargo.Routed(s.trackingId, s.routeSpec, i)),
                List(Event.AssignedRoute(s.trackingId, i))
              )
            case s => (s, List.empty[Event])
          }
          case ChangeDestination(d) => State {
            case s: Cargo.Unrouted =>
              (
                s.copy(routeSpec = s.routeSpec.copy(destination = d)),
                List(Event.ChangedDestination(s.trackingId, d))
              )
            case s: (Cargo.Routed | Cargo.Misrouted) =>
              (
                inspectRoute(Cargo.Routed(s.trackingId, s.routeSpec.copy(destination = d), s.itinerary)),
                List(Event.ChangedDestination(s.trackingId, d))
              )
            case s => (s, List.empty[Event])
          }
          case ChangeDeadline(d) => State {
            case s: Cargo.Unrouted if d.isFuture =>
              (
                s.copy(routeSpec = s.routeSpec.copy(deadline = d)),
                List(Event.ChangedDeadline(s.trackingId, d))
              )
            case s: (Cargo.Routed | Cargo.Misrouted) if d.isFuture =>
              (
                inspectRoute(Cargo.Routed(s.trackingId, s.routeSpec.copy(deadline = d), s.itinerary)),
                List(Event.ChangedDeadline(s.trackingId, d))
              )
            case s => (s, List.empty[Event])
          }
          case Close() => State {
            case s: (Cargo.Routed | Cargo.Misrouted) =>
              (Cargo.Closed(s.trackingId, s.routeSpec, s.itinerary), List(Event.Closed(s.trackingId)))
            case s => (s, List.empty[Event])
          }
        }
    }
  end interpret

  private def isSatisfiedWithRoute(itinerary: Itinerary, routeSpec: RouteSpecification): Boolean =
    val fst: Option[LocationTime] = Some(LocationTime(routeSpec.origin, LocalDateTime.MIN))

    val lst = itinerary.legs.foldLeft(fst)((acc, leg) =>
      for {
        c <- acc
        if c.location == leg.load.location && c.time.isBeforeOrEq(leg.load.time)
      } yield leg.unload
    )

    lst.exists(lt => lt.location == routeSpec.destination && lt.time.isBefore(routeSpec.deadline))

  private def inspectRoute(state: Cargo.Routed): Cargo =
    if (isSatisfiedWithRoute(state.itinerary, state.routeSpec))
      state
    else
      Cargo.Misrouted(state.trackingId, state.routeSpec, state.itinerary)

  extension (s: String)
    def nonBlank: Boolean = !s.isBlank

  extension (d: Date)
    def isFuture: Boolean = d.isAfter(LocalDateTime.now())
    def isBeforeOrEq(t: Date): Boolean = d.compareTo(t) <= 0

end CargoAction