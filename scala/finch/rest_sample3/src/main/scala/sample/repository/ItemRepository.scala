package sample.repository

import sample.model.{Item, ItemId}

import scala.util.Try

trait ItemRepository {
  def store(item: Item): Try[Item]

  def restore(id: ItemId): Try[Option[Item]]

  def delete(id: ItemId): Try[Option[Item]]
}
