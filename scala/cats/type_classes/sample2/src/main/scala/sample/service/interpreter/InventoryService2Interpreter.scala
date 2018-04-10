package sample.service.interpreter

import cats.FlatMap
import scala.collection.mutable

import sample.model.InventoryItem
import sample.model.common.{Amount, ItemId}

object InventoryService2 extends InventoryService2Interpreter {
  implicit val optionFlatMap: FlatMap[Option] = cats.implicits.catsStdInstancesForOption
}

class InventoryService2Interpreter
  extends sample.service.InventoryService2[Option, ItemId, InventoryItem, Amount] {

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
