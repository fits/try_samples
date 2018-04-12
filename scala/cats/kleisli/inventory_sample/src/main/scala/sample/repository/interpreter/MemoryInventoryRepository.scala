package sample.repository.interpreter

import cats.implicits._
import scala.collection.mutable

import sample.model.InventoryItem
import sample.model.common.ItemId
import sample.repository.InventoryRepository
import sample.repository.RepoValid

object MemoryInventoryRepository extends MemoryInventoryRepository

class MemoryInventoryRepository extends InventoryRepository {
  private lazy val repo = mutable.Map.empty[ItemId, InventoryItem]

  override def store(s: InventoryItem): RepoValid[InventoryItem] = {
    repo += ((s.id, s))
    s.asRight
  }

  override def find(id: ItemId): RepoValid[Option[InventoryItem]] =
    repo.get(id).asRight
}
