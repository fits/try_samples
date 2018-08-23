package sample.model

object common {
  type ItemId = String
  type Amount = Int
}

import common._

case class InventoryItem(id: ItemId, quantity: Amount = 0)
