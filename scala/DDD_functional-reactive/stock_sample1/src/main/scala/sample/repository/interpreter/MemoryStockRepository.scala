package sample.repository.interpreter

import sample.model.{Stock, StockId}
import sample.repository.StockRepository

import scala.collection.mutable
import scala.util.{Success, Try}

class MemoryStockRepository extends StockRepository[Stock, StockId] {
  private lazy val repo = mutable.Map.empty[StockId, Stock]

  override def find(id: StockId): Try[Option[Stock]] = Success(repo.get(id))

  override def save(stock: Stock): Try[Stock] = {
    repo += ((stock.stockId, stock))
    Success(stock)
  }
}
