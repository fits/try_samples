package sample.service

import cats.data.Kleisli
import sample.repository.StockRepository

import scala.util.Try

trait StockService[F[_], Stock, StockId, ItemId, Quantity] {

  def current(stockId: StockId): F[Option[Stock]]
  def regist(itemId: ItemId, managed: Boolean): F[Stock]
  def move(from: StockId, to: StockId, qty: Quantity): F[(Stock, Stock)]
}
