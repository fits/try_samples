package sample.service.interpreter

import cats.Monad

import sample.free.InventoryOp
import sample.model.InventoryItem
import sample.model.common.{Amount, ItemId}
import sample.repository.InventoryRepository

object InventoryService extends sample.service.InventoryService[ItemId, InventoryItem, Amount]
  with InventoryRepository {

  override def current(id: ItemId): InventoryOp[InventoryItem] = find(id)

  override def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem] =
    for {
      c <- current(id)
      r <- store(c.copy(quantity = c.quantity + quantity))
    } yield r

  override def remove(id: ItemId, quantity: Amount): InventoryOp[Option[InventoryItem]] =
    for {
      c <- current(id)
      r <-
        if (c.quantity >= quantity)
          store(c.copy(quantity = c.quantity - quantity)).map(Some(_))
        else
          Monad[InventoryOp].pure(None)
    } yield r
}
