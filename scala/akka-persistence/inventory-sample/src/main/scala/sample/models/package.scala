package sample

package object models {
  case class InventoryItem(id: String, name: String = "", count: Int = 0)
}
