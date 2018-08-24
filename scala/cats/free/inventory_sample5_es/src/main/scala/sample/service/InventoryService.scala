package sample.service

import cats.data.{EitherT, State}
import cats.free.Free
import cats.{Id, ~>}
import sample.es._
import sample.model.InventoryItem
import sample.model.common.{Amount, ItemId}

import scala.collection.mutable
import scala.concurrent.Future

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

object EventStoreInterpreter {
  type EitherErr[A] = EitherT[Id, Throwable, A]

  private def left[A] = EitherT.leftT[Id, A]
  private def right = EitherT.rightT[Id, Throwable]

  private val eventStore = new {
    private lazy val store = mutable.Map.empty[ItemId, List[Event[_]]]

    def get(key: ItemId): EitherErr[List[Event[_]]] =
      right(store.getOrElse(key, List.empty[Event[_]]))

    def add(key: ItemId, event: Event[_]): EitherErr[Event[_]] =
      get(key).map { es =>
        store.put(key, es :+ event)
        event
      }
  }

  val step = new (Event ~> EitherErr) {
    override def apply[A](fa: Event[A]): EitherErr[A] = fa match {
      case c @ Created(id) => validateCreate(id).flatMap(_ => eventStore.add(id, c)).map(_ => id)
      case a @ Added(id, _) => validateUpdate(id).flatMap(_ => eventStore.add(id, a)).map(_ => ())
      case r @ Removed(id, qty) => validateQuantity(id, qty).flatMap(_ => eventStore.add(id, r)).map(_ => ())
    }
  }

  def interpret[A](c: Command[A]): EitherErr[A] = c.foldMap(step)

  def restore(id: ItemId): EitherErr[InventoryItem] =
    eventStore.get(id).map(_.foldLeft(InventoryItem("")) { (acc, ev) =>
      ev match {
        case Created(id) => InventoryItem(id)
        case Added(_, qty) => acc.copy(quantity = acc.quantity + qty)
        case Removed(_, qty) => acc.copy(quantity = acc.quantity - qty)
      }
    })

  private def validateCreate(id: ItemId): EitherErr[ItemId] =
    eventStore.get(id).flatMap(es =>
      if (es.isEmpty) right(id) else left(new Exception("not empty")))

  private def validateUpdate(id: ItemId): EitherErr[ItemId] =
    eventStore.get(id).flatMap(es =>
      if (es.isEmpty) left(new Exception("is empty")) else right(id))

  private def validateQuantity(id: ItemId, qty: Amount): EitherErr[InventoryItem] =
    restore(id).flatMap(inv =>
      if (inv.quantity < qty) left(new Exception(s"quantity < ${qty}")) else right(inv))
}