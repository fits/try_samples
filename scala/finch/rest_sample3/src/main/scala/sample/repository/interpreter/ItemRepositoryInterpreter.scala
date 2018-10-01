package sample.repository.interpreter

import sample.model.{Item, ItemId}
import sample.repository.ItemRepository

import scala.collection.mutable
import scala.util.{Success, Try}

object ItemRepositoryInterpreter extends ItemRepository {
  private lazy val repo = mutable.Map.empty[ItemId, Item]

  override def store(item: Item): Try[Item] = {
    repo += ((item.id, item))
    Success(item)
  }

  override def restore(id: ItemId): Try[Option[Item]] = Success(repo.get(id))

  override def delete(id: ItemId): Try[Option[Item]] = Success(repo.remove(id))
}
