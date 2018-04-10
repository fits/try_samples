package sample.service.interpreter

import scala.collection.mutable

import sample.model.InventoryItem
import sample.model.common.{Amount, ItemId}

object InventoryService extends InventoryServiceInterpreter

class InventoryServiceInterpreter
  extends sample.service.InventoryService[ItemId, InventoryItem, Amount] {

  private lazy val repo = mutable.Map.empty[ItemId, InventoryItem]

  override def current(id: ItemId): InventoryOp[InventoryItem] =
    repo.get(id).orElse(Some(InventoryItem(id)))

  override def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem] =
    for {
      c <- current(id)
      r <- save(c.copy(quantity = c.quantity + quantity))
    } yield r

  override def remove(id: ItemId, quantity: Amount): InventoryOp[InventoryItem] =
    for {
      c <- current(id).filter(p => p.quantity >= quantity)
      r <- save(c.copy(quantity = c.quantity - quantity))
    } yield r

  private def save(item: InventoryItem): InventoryOp[InventoryItem] = {
    repo += ((item.id, item))
    Some(item)
  }
}
