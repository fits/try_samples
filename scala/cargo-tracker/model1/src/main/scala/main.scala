
import cats.{Id, ~>}
import cats.arrow.FunctionK
import cats.free.Free
import Free.liftF

import scala.collection.mutable
import java.time.LocalDateTime

type TrackingId = String
type UnLocode = String
type VoyageNo = String
type Date = LocalDateTime

case class RouteSpecification(origin: UnLocode, destination: UnLocode, deadline: Date)

case class Itinerary(legs: List[Leg])

case class LocationTime(location: UnLocode, time: Date)

case class Leg(voyageNo: VoyageNo, load: LocationTime, unload: LocationTime)

trait HasTrackingId {
  val trackingId: TrackingId
}

trait HasRouteSpec {
  val routeSpec: RouteSpecification
}

trait HasItinerary extends HasRouteSpec {
  val itinerary: Itinerary
}

enum Cargo extends HasTrackingId:
  case Empty(trackingId: TrackingId = "")
  case Unrouted(trackingId: TrackingId, routeSpec: RouteSpecification) extends Cargo, HasRouteSpec
  case Routed(trackingId: TrackingId, routeSpec: RouteSpecification, itinerary: Itinerary) extends Cargo, HasItinerary
  case Misrouted(trackingId: TrackingId, routeSpec: RouteSpecification, itinerary: Itinerary) extends Cargo, HasItinerary
  case Closed(trackingId: TrackingId, routeSpec: RouteSpecification, itinerary: Itinerary) extends Cargo, HasItinerary

enum CommandA[A] extends HasTrackingId:
  case Find(trackingId: TrackingId) extends CommandA[Option[Cargo]]
  case Create(trackingId: TrackingId, routeSpec: RouteSpecification) extends CommandA[Option[Cargo]]
  case AssignRoute(trackingId: TrackingId, itinerary: Itinerary) extends CommandA[Option[Cargo]]
  case ChangeDestination(trackingId: TrackingId, newDestination: UnLocode) extends CommandA[Option[Cargo]]
  case ChangeDeadline(trackingId: TrackingId, newDeadline: Date) extends CommandA[Option[Cargo]]
  case Close(trackingId: TrackingId) extends CommandA[Option[Cargo]]

type Command[A] = Free[CommandA, A]

import CommandA.*

object CargoAction:
  extension (s: String)
    def nonBlank: Boolean = !s.isBlank

  extension (d: Date)
    def isFuture: Boolean = d.isAfter(LocalDateTime.now())
    def isBeforeOrEq(t: Date): Boolean = d.compareTo(t) <= 0

  extension (s: Cargo)
    def action(cmd: CommandA[Option[Cargo]]): Option[Cargo] =
      if (s == Cargo.Empty())
        cmd match
          case Create(t, r) if t.nonBlank && r.deadline.isFuture => Some(Cargo.Unrouted(t, r))
          case _ => None
      else if (s.trackingId == cmd.trackingId)
        s match
          case s @ Cargo.Unrouted(_, _) => transitForUnrouted(s, cmd)
          case s @ (Cargo.Routed(_, _, _) | Cargo.Misrouted(_, _, _)) => transitForRouted(s, cmd)
          case Cargo.Empty(_) | Cargo.Closed(_, _, _) => None
      else
        None

  private def transitForUnrouted[A](state: Cargo.Unrouted, cmd: CommandA[A]): Option[Cargo] = cmd match
    case AssignRoute(_, i) if i.legs.nonEmpty =>
      Some(inspectRoute(Cargo.Routed(state.trackingId, state.routeSpec, i)))
    case ChangeDestination(_, d) =>
      Some(state.copy(routeSpec = state.routeSpec.copy(destination = d)))
    case ChangeDeadline(_, d) if d.isFuture =>
      Some(state.copy(routeSpec = state.routeSpec.copy(deadline = d)))
    case _ => None

  private def transitForRouted[A](state: Cargo.Routed | Cargo.Misrouted, cmd: CommandA[A]): Option[Cargo] = cmd match
    case AssignRoute(_, i) if i.legs.nonEmpty =>
      Some(inspectRoute(Cargo.Routed(state.trackingId, state.routeSpec, i)))
    case ChangeDestination(_, d) =>
      val r = state.routeSpec.copy(destination = d)
      Some(inspectRoute(Cargo.Routed(state.trackingId, r, state.itinerary)))
    case ChangeDeadline(_, d) if d.isFuture =>
      val r = state.routeSpec.copy(deadline = d)
      Some(inspectRoute(Cargo.Routed(state.trackingId, r, state.itinerary)))
    case Close(_) =>
      Some(Cargo.Closed(state.trackingId, state.routeSpec, state.itinerary))
    case _ => None

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

end CargoAction

object action:
  def find(trackingId: TrackingId): Command[Option[Cargo]] = liftF(Find(trackingId))

  def create(trackingId: TrackingId, origin: UnLocode, destination: UnLocode, deadline: Date): Command[Option[Cargo]] =
    liftF(Create(trackingId, RouteSpecification(origin, destination, deadline)))

  def assignRoute(trackingId: TrackingId, itinerary: Itinerary): Command[Option[Cargo]] =
    liftF(AssignRoute(trackingId, itinerary))

  def changeDestination(trackingId: TrackingId, destination: UnLocode): Command[Option[Cargo]] =
    liftF(ChangeDestination(trackingId, destination))

  def changeDeadline(trackingId: TrackingId, deadline: Date): Command[Option[Cargo]] =
    liftF(ChangeDeadline(trackingId, deadline))

  def close(trackingId: TrackingId): Command[Option[Cargo]] = liftF(Close(trackingId))
end action

def interpret(using store: mutable.Map[TrackingId, Cargo]): CommandA ~> Id =
  new (CommandA ~> Id) {
    import CargoAction.action

    val save = (s: Cargo) => {
      store.put(s.trackingId, s)
      s
    }

    def apply[A](cmd: CommandA[A]): Id[A] =
      cmd match {
        case Find(t) => store.get(t)
        case c @ (Create(_, _) | AssignRoute(_, _) | ChangeDestination(_, _) | ChangeDeadline(_, _) | Close(_)) =>
          store.getOrElse(c.trackingId, Cargo.Empty()).action(c).map(save)
      }
  }
end interpret

import action.*

@main def main(): Unit =
  given store: mutable.Map[TrackingId, Cargo] = mutable.Map.empty[TrackingId, Cargo]

  val now = LocalDateTime.now()

  val d1 = for {
    _ <- create("id-1", "USNYC", "JNTKO", now.plusDays(10))
    _ <- assignRoute(
      "id-1",
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", now.plusDays(1)), LocationTime("CNSHA", now.plusDays(4))),
        Leg("0200A", LocationTime("CNSHA", now.plusDays(5)), LocationTime("JNTKO", now.plusDays(7)))
      ))
    )
    _ <- close("id-1")
    r <- find("id-1")
  } yield r

  println(d1.foldMap(interpret))

  val d2 = for {
    _ <- create("id-2", "USNYC", "JNTKO", LocalDateTime.now().plusDays(10))
    _ <- assignRoute("id-2", Itinerary(List.empty))
    r <- find("id-2")
  } yield r

  println(d2.foldMap(interpret))

  val d3 = for {
    _ <- create("id-3", "USNYC", "JNTKO", now.plusDays(10))
    _ <- assignRoute(
      "id-3",
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", now.plusDays(1)), LocationTime("CNSHA", now.plusDays(4))),
        Leg("0200A", LocationTime("CNSHA", now.plusDays(5)), LocationTime("JNTKO", now.plusDays(7)))
      ))
    )
    r <- find("id-3")
  } yield r

  println(d3.foldMap(interpret))

  val d4 = for {
    _ <- create("id-4", "USNYC", "JNTKO", now.plusDays(10))
    _ <- assignRoute(
      "id-4",
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", now.plusDays(1)), LocationTime("CNSHA", now.plusDays(4))),
        Leg("0200A", LocationTime("CNSHA", now.plusDays(5)), LocationTime("AUMEL", now.plusDays(7)))
      ))
    )
    r <- find("id-4")
  } yield r

  println(d4.foldMap(interpret))

  val d5 = for {
    _ <- create("id-5", "USNYC", "JNTKO", now.plusDays(10))
    _ <- assignRoute(
      "id-5",
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", now.plusDays(1)), LocationTime("CNSHA", now.plusDays(4))),
        Leg("0200A", LocationTime("CNSHA", now.plusDays(5)), LocationTime("AUMEL", now.plusDays(7)))
      ))
    )
    _ <- changeDestination("id-5", "AUMEL")
    r <- find("id-5")
  } yield r

  println(d5.foldMap(interpret))

  println(store)