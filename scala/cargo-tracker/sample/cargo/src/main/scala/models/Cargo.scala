package models

import cats.implicits.*
import cats.data.StateT

import java.time.OffsetDateTime

type TrackingId = String
type UnLocode = String
type VoyageNo = String
type Date = OffsetDateTime

case class RouteSpec(origin: UnLocode, destination: UnLocode, deadline: Date)

case class Itinerary(legs: List[Leg])

case class LocationTime(location: UnLocode, time: Date)

case class Leg(voyageNo: VoyageNo, load: LocationTime, unload: LocationTime)

trait HasRouteSpec {
  val trackingId: TrackingId
  val routeSpec: RouteSpec
}

trait HasItinerary extends HasRouteSpec {
  val itinerary: Itinerary
}

enum Cargo:
  case Empty()
  case Unrouted(trackingId: TrackingId, routeSpec: RouteSpec) extends Cargo, HasRouteSpec
  case Routed(trackingId: TrackingId, routeSpec: RouteSpec, itinerary: Itinerary) extends Cargo, HasItinerary
  case Misrouted(trackingId: TrackingId, routeSpec: RouteSpec, itinerary: Itinerary) extends Cargo, HasItinerary
  case Closed(trackingId: TrackingId, routeSpec: RouteSpec, itinerary: Itinerary) extends Cargo, HasItinerary

enum Event:
  case Created(trackingId: TrackingId, routeSpec: RouteSpec)
  case AssignedRoute(trackingId: TrackingId, itinerary: Itinerary)
  case ChangedDestination(trackingId: TrackingId, newDestination: UnLocode)
  case ChangedDeadline(trackingId: TrackingId, newDeadline: Date)
  case Closed(trackingId: TrackingId)

enum CommandA[A]:
  case Create(trackingId: TrackingId, routeSpec: RouteSpec) extends CommandA[Event]
  case AssignRoute(itinerary: Itinerary) extends CommandA[Event]
  case ChangeDestination(newDestination: UnLocode) extends CommandA[Event]
  case ChangeDeadline(newDeadline: Date) extends CommandA[Event]
  case Close() extends CommandA[Event]
  case IsDestination(location: UnLocode) extends CommandA[Boolean]
  case IsOnRoute(location: UnLocode, voyageNo: Option[VoyageNo]) extends CommandA[Option[Boolean]]

enum CommandError:
  case InvalidState()
  case BlankId()
  case PastDeadline()
  case EmptyLegs()
  case NoChange(name: String)

type ErrorOr[A] = Either[CommandError, A]
type CargoState[A] = StateT[ErrorOr, Cargo, A]

object Cargo:
  import CommandA.*

  def emptyCargo = Cargo.Empty()

  def toId(state: Cargo): Option[TrackingId] = state match {
    case c: HasRouteSpec => Some(c.trackingId)
    case _ => None
  }

  def action[A](cmd: CommandA[A]): CargoState[A] = cmd match
    case Create(t, r) => StateT {
      case Cargo.Empty() =>
        if t.isBlank then
          Either.left(CommandError.BlankId())
        else if !r.deadline.isFuture then
          Either.left(CommandError.PastDeadline())
        else
          Either.right((Cargo.Unrouted(t, r), Event.Created(t, r)))
      case _ => Either.left(CommandError.InvalidState())
    }
    case AssignRoute(i) => StateT {
      case Cargo.Unrouted(t, r) =>
        if i.legs.isEmpty then
          Either.left(CommandError.EmptyLegs())
        else
          Either.right((
            inspectRoute(Cargo.Routed(t, r, i)),
            Event.AssignedRoute(t, i)

          ))
      case s: (Cargo.Routed | Cargo.Misrouted) =>
        if i.legs.isEmpty then
          Either.left(CommandError.EmptyLegs())
        else if i == s.itinerary then
          Either.left(CommandError.NoChange("itinerary"))
        else
          Either.right((
            inspectRoute(Cargo.Routed(s.trackingId, s.routeSpec, i)),
            Event.AssignedRoute(s.trackingId, i)
          ))
      case _ => Either.left(CommandError.InvalidState())
    }
    case Close() => StateT {
      case s: (Cargo.Routed | Cargo.Misrouted) =>
        Either.right((
          Cargo.Closed(s.trackingId, s.routeSpec, s.itinerary),
          Event.Closed(s.trackingId)
        ))
      case _ => Either.left(CommandError.InvalidState())
    }
    case ChangeDestination(d) => StateT {
      case s: Cargo.Unrouted =>
        if d == s.routeSpec.destination then
          Either.left(CommandError.NoChange("destination"))
        else
          Either.right((
            s.copy(routeSpec = s.routeSpec.copy(destination = d)),
            Event.ChangedDestination(s.trackingId, d)
          ))
      case s: (Cargo.Routed | Cargo.Misrouted) =>
        if d == s.routeSpec.destination then
          Either.left(CommandError.NoChange("destination"))
        else
          Either.right((
            inspectRoute(Cargo.Routed(s.trackingId, s.routeSpec.copy(destination = d), s.itinerary)),
            Event.ChangedDestination(s.trackingId, d)
          ))
      case _ => Either.left(CommandError.InvalidState())
    }
    case ChangeDeadline(d) => StateT {
      case s: Cargo.Unrouted =>
        if !d.isFuture then
          Either.left(CommandError.PastDeadline())
        else if d == s.routeSpec.deadline then
          Either.left(CommandError.NoChange("deadline"))
        else
          Either.right((
            s.copy(routeSpec = s.routeSpec.copy(deadline = d)),
            Event.ChangedDeadline(s.trackingId, d)
          ))
      case s: (Cargo.Routed | Cargo.Misrouted) =>
        if !d.isFuture then
          Either.left(CommandError.PastDeadline())
        else if d == s.routeSpec.deadline then
          Either.left(CommandError.NoChange("deadline"))
        else
          Either.right((
            inspectRoute(Cargo.Routed(s.trackingId, s.routeSpec.copy(deadline = d), s.itinerary)),
            Event.ChangedDeadline(s.trackingId, d)
          ))
      case _ => Either.left(CommandError.InvalidState())
    }
    case IsDestination(l) => StateT {
      case s: HasItinerary =>
        Either.right((s, s.routeSpec.destination == l || s.itinerary.legs.last.unload.location == l))
      case s: HasRouteSpec =>
        Either.right((s, s.routeSpec.destination == l))
      case s =>
        Either.right((s, false))
    }
    case IsOnRoute(l, v) => StateT {
      case s: Closed => Either.right((s, None))
      case s: HasItinerary =>
        val r = s.itinerary.legs.exists { leg =>
          v.map(_ == leg.voyageNo).getOrElse(true) && (leg.load.location == l || leg.unload.location == l)
        }
        Either.right((s, Some(r)))
      case s => Either.right((s, None))
    }
  end action

  private def isSatisfiedWithRoute(itinerary: Itinerary, routeSpec: RouteSpec): Boolean =
    val fst: Option[LocationTime] = Some(LocationTime(routeSpec.origin, OffsetDateTime.MIN))

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

  extension (d: Date)
    def isFuture: Boolean = d.isAfter(OffsetDateTime.now())
    def isBeforeOrEq(t: Date): Boolean = d.compareTo(t) <= 0

end Cargo
