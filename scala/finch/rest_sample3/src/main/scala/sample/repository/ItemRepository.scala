package sample.repository

import sample.model.{Item, ItemId}

import scala.concurrent.Future

trait ItemRepository {
  def store(item: Item): Future[Item]

  def restore(id: ItemId): Future[Option[Item]]

  def delete(id: ItemId): Future[Option[Item]]
}
