package sample.service

import cats.Monad
import sample.free.InventoryOp

trait InventoryService[ItemId, InventoryItem, Amount] {
  def current(id: ItemId): InventoryOp[InventoryItem]
  def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]
  def remove(id: ItemId, quantity: Amount): InventoryOp[Option[InventoryItem]]

  def move(from: ItemId, to: ItemId, quantity: Amount):
      InventoryOp[Option[(InventoryItem, InventoryItem)]] =
    for {
      r <- remove(from, quantity)
      d <-
        if (r.isDefined)
          add(to, quantity).map(Some(r.get, _))
        else
          Monad[InventoryOp].pure(None)
    } yield d
}