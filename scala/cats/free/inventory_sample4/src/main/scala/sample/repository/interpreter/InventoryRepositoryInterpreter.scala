package sample.repository.interpreter

import cats.~>

import scala.collection.mutable
import scala.concurrent.Future

import sample.free.InventoryOp
import sample.model.InventoryItem
import sample.model.common.ItemId
import sample.repository.{Find, InventoryOpF, Store}

trait InventoryOpInterpreter[F[_]] {
  def interpret[A](act: InventoryOp[A]): F[A]
}

object common {
  type FutureEither[A] = Future[Either[String, A]]
}

import common._

object MemoryInventoryOpInterpreter extends InventoryOpInterpreter[FutureEither] {
  import cats.implicits._
  import scala.concurrent.ExecutionContext.Implicits.global

  private lazy val repo = mutable.Map.empty[ItemId, InventoryItem]

  val step: InventoryOpF ~> Future  = new (InventoryOpF ~> Future) {
    override def apply[A](fa: InventoryOpF[A]): Future[A] = fa match {
      case Store(item) =>
        repo += ((item.id, item))
        Future(item.asInstanceOf[A])
      case Find(id) => Future(repo.getOrElse(id, InventoryItem(id)).asInstanceOf[A])
    }
  }

  override def interpret[A](act: InventoryOp[A]): FutureEither[A] =
    act.value.foldMap(step)
}
