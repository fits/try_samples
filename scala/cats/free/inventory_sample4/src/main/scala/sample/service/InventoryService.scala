package sample.service

import sample.free.InventoryOp

trait InventoryService[ItemId, InventoryItem, Amount] {
  def current(id: ItemId): InventoryOp[InventoryItem]
  def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]
  def remove(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]

  def move(from: ItemId, to: ItemId, quantity: Amount):
      InventoryOp[(InventoryItem, InventoryItem)] =
    for {
      a <- remove(from, quantity)
      b <- add(to, quantity)
    } yield (a, b)
}