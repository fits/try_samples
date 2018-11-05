package sample.model

sealed trait Stock {
  val stockId: StockId
  val itemId: ItemId
  val in: Quantity
  val out: Quantity
}

sealed case class UnmanagedStock(stockId: StockId, itemId: ItemId,
                                 in: Quantity = 0, out: Quantity = 0) extends Stock

sealed case class ManagedStock (stockId: StockId, itemId: ItemId,
                                in: Quantity = 0, out: Quantity = 0) extends Stock {
  val qty: Quantity = in - out
}
