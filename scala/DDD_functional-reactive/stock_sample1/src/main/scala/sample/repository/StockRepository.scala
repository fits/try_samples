package sample.repository

import scala.util.Try

trait StockRepository[Stock, StockId] {
  def find(id: StockId): Try[Option[Stock]]
  def save(stock: Stock): Try[Stock]
}
