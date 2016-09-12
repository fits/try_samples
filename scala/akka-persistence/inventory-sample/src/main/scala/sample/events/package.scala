package sample

package object events {
  case class InventoryItemCreated(id: String)
  case class InventoryItemRenamed(newName: String)
  case class ItemsCheckedInToInventory(count: Int)
}
