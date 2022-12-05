package models.delivery

import cats.{Id, ~>}
import cats.data.{Kleisli, State}
import cats.free.Free.liftF
import models.*
import models.cargo.TrackingId

enum Delivery:
  case NotReceived(trackingId: TrackingId)
  case InPort(trackingId: TrackingId, location: UnLocode)
  case OnBoardCarrier(trackingId: TrackingId, currentVoyageNo: VoyageNo, location: UnLocode)
  case Claimed(trackingId: TrackingId, location: UnLocode, claimedTime: Date)

enum HandlingEvent:
  case Received(trackingId: TrackingId, location: UnLocode, completionTime: Date)
  case Loaded(trackingId: TrackingId, voyageNo: VoyageNo, location: UnLocode, completionTime: Date)
  case Unloaded(trackingId: TrackingId, voyageNo: VoyageNo, location: UnLocode, completionTime: Date)
  case Claimed(trackingId: TrackingId, location: UnLocode, completionTime: Date)

type FindRoute = TrackingId => Option[Itinerary]
type FindRouteAndSpec = TrackingId => Option[(Itinerary, RouteSpecification)]

enum CommandA[A]:
  case Receive(location: UnLocode, completionTime: Date) extends CommandA[List[HandlingEvent]]
  case Load(voyageNo: VoyageNo, completionTime: Date, findRoute: FindRoute) extends CommandA[List[HandlingEvent]]
  case Unload(location: UnLocode, completionTime: Date) extends CommandA[List[HandlingEvent]]
  case Claim(completionTime: Date) extends CommandA[List[HandlingEvent]]
  case IsMisdirected(findRoute: FindRoute) extends CommandA[Boolean]
  case IsUnloadedAtDestination(findRouteAndSpec: FindRouteAndSpec) extends CommandA[Boolean]

type DeliveryState[A] = State[Delivery, A]

object Delivery:
  import CommandA.*

  def createDelivery(trackingId: TrackingId) = Delivery.NotReceived(trackingId)

  def receive(location: UnLocode, completionTime: Date) =
    liftF(Receive(location, completionTime))

  def load(voyageNo: VoyageNo, completionTime: Date, find: FindRoute) =
    liftF(Load(voyageNo, completionTime, find))

  def unload(location: UnLocode, completionTime: Date) =
    liftF(Unload(location, completionTime))

  def claim(completionTime: Date) =
    liftF(Claim(completionTime))

  def isMisdirected(findRoute: FindRoute) =
    liftF(IsMisdirected(findRoute))

  def isUnloadedAtDestination(findRouteAndSpec: FindRouteAndSpec) =
    liftF(IsUnloadedAtDestination(findRouteAndSpec))

  def action[A](cmd: CommandA[A]): Kleisli[Id, Delivery, (Delivery, A)] = cmd match
    case Receive(l, c) => Kleisli {
      case NotReceived(t) =>
        (InPort(t, l), List(HandlingEvent.Received(t, l, c)))
      case s => (s, List.empty[HandlingEvent])
    }
    case Load(v, c, f) => Kleisli {
      case InPort(t, l) if existsVoyage(f, t, v) =>
        (OnBoardCarrier(t, v, l), List(HandlingEvent.Loaded(t, v, l, c)))
      case s => (s, List.empty[HandlingEvent])
    }
    case Unload(l, c) => Kleisli {
      case OnBoardCarrier(t, v, _) =>
        (InPort(t, l), List(HandlingEvent.Unloaded(t, v, l, c)))
      case s => (s, List.empty[HandlingEvent])
    }
    case Claim(c) => Kleisli {
      case InPort(t, l) =>
        (Claimed(t, l, c), List(HandlingEvent.Claimed(t, l, c)))
      case s => (s, List.empty[HandlingEvent])
    }
    case IsMisdirected(f) => Kleisli {
      case s @ InPort(t, l) =>
        (s, !f(t).exists(_.legs.exists(leg => leg.load.location == l || leg.unload.location == l)))
      case s @ OnBoardCarrier(t, v, l) =>
        (s, !f(t).exists(_.legs.exists(leg => leg.voyageNo == v && leg.load.location == l)))
      case s @ Claimed(t, l, _) =>
        (s, !f(t).filter(_.legs.nonEmpty).exists(_.legs.last.unload.location == l))
      case s => (s, false)
    }
    case IsUnloadedAtDestination(f) => Kleisli {
      case s @ InPort(t, l) =>
        (s, f(t).exists((i, r) => r.destination == l || (i.legs.nonEmpty && i.legs.last.unload.location == l)))
      case s => (s, false)
    }
  end action

  def interpret: CommandA ~> DeliveryState =
    new (CommandA ~> DeliveryState) {
      def apply[A](cmd: CommandA[A]): DeliveryState[A] = State {
        action(cmd).run(_)
      }
    }

  def existsVoyage(findRoute: FindRoute, trackingId: TrackingId, voyageNo: VoyageNo): Boolean =
    findRoute(trackingId).exists(_.legs.exists(_.voyageNo == voyageNo))

end Delivery
