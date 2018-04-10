package sample.service

trait InventoryService[ItemId, InventoryItem, Amount] {
  type InventoryOp[A] = Option[A]

  def current(id: ItemId): InventoryOp[InventoryItem]
  def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]
  def remove(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]

  def move(from: ItemId, to: ItemId, quantity: Amount): InventoryOp[(InventoryItem, InventoryItem)] =
    for {
      r <- remove(from, quantity)
      a <- add(to, quantity)
    } yield (r, a)
}
