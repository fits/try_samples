package sample.service.interpreter

import cats.data.EitherT

import sample.free.InventoryOp
import sample.model.InventoryItem
import sample.model.common.{Amount, ItemId}
import sample.repository.InventoryRepository

object InventoryService extends sample.service.InventoryService[ItemId, InventoryItem, Amount]
  with InventoryRepository {

  override def current(id: ItemId): InventoryOp[InventoryItem] = EitherT.right(find(id))

  override def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem] =
    for {
      c <- current(id)
      r <- storeEither(c.copy(quantity = c.quantity + quantity))
    } yield r

  override def remove(id: ItemId, quantity: Amount): InventoryOp[InventoryItem] =
    for {
      c <- current(id)
      r <- storeEither(c.copy(quantity = c.quantity - quantity))
    } yield r

  private def storeEither(s: InventoryItem): InventoryOp[InventoryItem] =
    if (s.quantity >= 0)
      EitherT.right(store(s))
    else
      EitherT.leftT("invalid quantity")
}
