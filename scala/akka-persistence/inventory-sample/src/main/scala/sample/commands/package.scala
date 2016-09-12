package sample

package object commands {
  case class CreateInventoryItem(id: String, name: String)
  case class CheckInItemsToInventory(id: String, count: Int)
}
