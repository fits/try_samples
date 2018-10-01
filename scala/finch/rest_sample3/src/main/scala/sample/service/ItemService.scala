package sample.service

import cats.data.Kleisli

import sample.model.{Amount, Item, ItemId}
import sample.repository.ItemRepository

import scala.concurrent.Future

trait ItemService {
  type ItemOp[A] = Kleisli[Future, ItemRepository, A]

  def createItem(name: String, price: Amount): ItemOp[Item]

  def getItem(id: ItemId): ItemOp[Option[Item]]

  def deleteItem(id: ItemId): ItemOp[Option[Item]]
}
