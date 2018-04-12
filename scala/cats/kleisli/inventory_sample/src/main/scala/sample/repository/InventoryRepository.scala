package sample.repository

import sample.model.InventoryItem
import sample.model.common.ItemId

trait InventoryRepository {
  def store(s: InventoryItem): RepoValid[InventoryItem]
  def find(id: ItemId): RepoValid[Option[InventoryItem]]
}
