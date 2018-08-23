package sample.service

import cats.data.State
import cats.free.Free
import cats.~>
import sample.es._
import sample.model.InventoryItem
import sample.model.common.{Amount, ItemId}

case class Created(id: ItemId) extends Event[ItemId]
case class Added(id: ItemId, quantity: Amount) extends Event[Unit]
case class Removed(id: ItemId, quantity: Amount) extends Event[Unit]

object InventoryService {
  private implicit def liftEvent[A](event: Event[A]): Command[A] = Free.liftF(event)

  def create(id: ItemId): Command[ItemId] = Created(id)
  def add(id: ItemId, quantity: Amount): Command[Unit] = Added(id, quantity)
  def remove(id: ItemId, quantity: Amount): Command[Unit] = Removed(id, quantity)

  def move(from: ItemId, to: ItemId, quantity: Amount):
      Command[Unit] =
    for {
      _ <- remove(from, quantity)
      _ <- add(to, quantity)
    } yield ()
}

object InventoryInterpreter {
  type StateM[A] = State[Map[ItemId, InventoryItem], A]

  val step = new (Event ~> StateM) {
    override def apply[A](fa: Event[A]): StateM[A] = fa match {
      case c @ Created(id) => State { s => (updateState(c, s), id) }
      case a: Added => State { s => (updateState(a, s), ()) }
      case r: Removed => State { s => (updateState(r, s), ()) }
    }
  }

  def interpret[A](c: Command[A]): StateM[A] = c.foldMap(step)

  private def updateState(ev: Event[_], state: Map[ItemId, InventoryItem]) = ev match {
    case Created(id) => state + (id -> InventoryItem(id, 0))
    case Added(id, qt) => state + (id -> state(id).copy(quantity = state(id).quantity + qt))
    case Removed(id, qt) => state + (id -> state(id).copy(quantity = state(id).quantity - qt))
  }
}