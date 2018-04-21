package sample.repository

import cats.free.Free

import sample.free.InventoryOp
import sample.model.InventoryItem
import sample.model.common.ItemId

sealed trait InventoryOpF[A]

case class Store(item: InventoryItem) extends InventoryOpF[InventoryItem]
case class Find(id: ItemId) extends InventoryOpF[InventoryItem]

object InventoryRepository extends InventoryRepository

trait InventoryRepository {
  def store(s: InventoryItem): InventoryOp[InventoryItem] =
    Free.liftF(Store(s))

  def find(id: ItemId): InventoryOp[InventoryItem] =
    Free.liftF(Find(id))
}
