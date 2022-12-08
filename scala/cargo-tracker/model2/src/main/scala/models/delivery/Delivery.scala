package models.delivery

import cats.{Id, ~>}
import cats.data.{Kleisli, State}
import cats.free.Free.liftF

import java.time.LocalDateTime

type TrackingId = String
type UnLocode = String
type VoyageNo = String
type Date = LocalDateTime

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

enum CommandA[A]:
  case Receive(location: UnLocode, completionTime: Date) extends CommandA[List[HandlingEvent]]
  case Load(voyageNo: VoyageNo, completionTime: Date) extends CommandA[List[HandlingEvent]]
  case Unload(location: UnLocode, completionTime: Date) extends CommandA[List[HandlingEvent]]
  case Claim(completionTime: Date) extends CommandA[List[HandlingEvent]]

type DeliveryState[A] = State[Delivery, A]

object Delivery:
  import CommandA.*

  def createDelivery(trackingId: TrackingId) = Delivery.NotReceived(trackingId)

  def receive(location: UnLocode, completionTime: Date) =
    liftF(Receive(location, completionTime))

  def load(voyageNo: VoyageNo, completionTime: Date) =
    liftF(Load(voyageNo, completionTime))

  def unload(location: UnLocode, completionTime: Date) =
    liftF(Unload(location, completionTime))

  def claim(completionTime: Date) =
    liftF(Claim(completionTime))

  def action[A](cmd: CommandA[A]): Kleisli[Id, Delivery, (Delivery, A)] = cmd match
    case Receive(l, c) => Kleisli {
      case NotReceived(t) =>
        (InPort(t, l), List(HandlingEvent.Received(t, l, c)))
      case s => (s, List.empty[HandlingEvent])
    }
    case Load(v, c) => Kleisli {
      case InPort(t, l) if v.nonBlank  =>
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
  end action

  def interpret: CommandA ~> DeliveryState =
    new (CommandA ~> DeliveryState) {
      def apply[A](cmd: CommandA[A]): DeliveryState[A] = State {
        action(cmd).run(_)
      }
    }

  extension (s: VoyageNo)
    def nonBlank: Boolean = !s.isBlank

end Delivery
