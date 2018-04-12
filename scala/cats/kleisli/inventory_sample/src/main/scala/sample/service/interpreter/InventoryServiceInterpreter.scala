package sample.service.interpreter

import cats.data.{Kleisli, NonEmptyList}
import cats.implicits._

import sample.model.InventoryItem
import sample.model.common.{Amount, ItemId}
import sample.repository.InventoryRepository
import sample.repository.RepoValid

object InventoryService extends InventoryServiceInterpreter

object common {
  type InvRepoOp[A] = Kleisli[RepoValid, InventoryRepository, A]
  def invRepoOp[A](f: InventoryRepository => RepoValid[A]): InvRepoOp[A] = Kleisli(f)
}

import common._

class InventoryServiceInterpreter
  extends sample.service.InventoryService[InvRepoOp, ItemId, InventoryItem, Amount] {

  override def current(id: ItemId): InventoryOp[InventoryItem] =
    invRepoOp(_.find(id).map(_.getOrElse(InventoryItem(id))))

  override def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem] =
    for {
      c <- current(id)
      r <- save(c.copy(quantity = c.quantity + quantity))
    } yield r

  override def remove(id: ItemId, quantity: Amount): InventoryOp[InventoryItem] =
    for {
      c <- current(id)
      _ <- invRepoOp(_ => validateQuantity(c, quantity))
      // 以下でも可
      // c <- current(id).andThen(validateQuantity(_, quantity))
      r <- save(c.copy(quantity = c.quantity - quantity))
    } yield r

  private def save(item: InventoryItem): InventoryOp[InventoryItem] =
    invRepoOp(_.store(item))

  private def validateQuantity(item: InventoryItem, quantity: Amount): RepoValid[InventoryItem] =
    if (item.quantity >= quantity) Right(item)
    else Left(NonEmptyList.one(s"quantity(= ${item.quantity}) is less than $quantity"))
}
