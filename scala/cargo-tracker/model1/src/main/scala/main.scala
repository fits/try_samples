
import cats.{Id, ~>}
import cats.arrow.FunctionK
import cats.free.Free
import Free.liftF

import scala.collection.mutable
import java.time.LocalDateTime

type TrackingId = String
type UnLocode = String
type VoyageNo = String

case class RouteSpecification(origin: UnLocode, destination: UnLocode, deadline: LocalDateTime)

case class Itinerary(legs: List[Leg])

case class LocationTime(location: UnLocode, time: LocalDateTime)

case class Leg(voyageNo: VoyageNo, load: LocationTime, unload: LocationTime)

enum Cargo:
  case Unrouted(trackingId: TrackingId, routeSpec: RouteSpecification)
  case Routed(trackingId: TrackingId, routeSpec: RouteSpecification, itinerary: Itinerary)

enum CommandA[A]:
  case Find(trackingId: TrackingId) extends CommandA[Option[Cargo]]
  case Create(trackingId: TrackingId, routeSpec: RouteSpecification) extends CommandA[Option[Cargo]]
  case AssignRoute(trackingId: TrackingId, itinerary: Itinerary) extends CommandA[Option[Cargo]]

type Command[A] = Free[CommandA, A]

import CommandA.*

object action:
  def find(trackingId: TrackingId): Command[Option[Cargo]] = liftF(Find(trackingId))

  def create(trackingId: TrackingId, origin: UnLocode, destination: UnLocode, deadline: LocalDateTime): Command[Option[Cargo]] =
    liftF(Create(trackingId, RouteSpecification(origin, destination, deadline)))

  def assignRoute(trackingId: TrackingId, itinerary: Itinerary): Command[Option[Cargo]] =
    liftF(AssignRoute(trackingId, itinerary))

def interpret: CommandA ~> Id =
  new (CommandA ~> Id) {
    val store = mutable.Map.empty[TrackingId, Cargo]

    def apply[A](fa: CommandA[A]): Id[A] =
      fa match {
        case Find(tid) => store.get(tid)
        case Create(tid, route) =>
          val d = Cargo.Unrouted(tid, route)
          store.put(tid, d)
          Some(d)
        case AssignRoute(tid, itinerary) => store.get(tid).flatMap(c => c match {
          case Cargo.Unrouted(trackingId, routeSpec) =>
            val d = Cargo.Routed(trackingId, routeSpec, itinerary)
            store.update(tid, d)
            Some(d)
          case _ => None
        })
      }
  }

import action.*

@main def main(): Unit =
  val d = for {
    _ <- create("id-1", "USNYC", "JNTKO", LocalDateTime.now().plusDays(10))
    _ <- assignRoute("id-1", Itinerary(List.empty))
    r <- find("id-1")
  } yield r

  val res = d.foldMap(interpret)

  println(res)
