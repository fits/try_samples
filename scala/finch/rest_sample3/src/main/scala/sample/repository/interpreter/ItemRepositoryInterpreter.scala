package sample.repository.interpreter

import sample.model.{Item, ItemId}
import sample.repository.ItemRepository

import scala.collection.mutable
import scala.concurrent.Future

object ItemRepositoryInterpreter extends ItemRepository {
  private lazy val repo = mutable.Map.empty[ItemId, Item]

  override def store(item: Item): Future[Item] = {
    repo += ((item.id, item))
    Future.successful(item)
  }

  override def restore(id: ItemId): Future[Option[Item]] = Future.successful(repo.get(id))

  override def delete(id: ItemId): Future[Option[Item]] = Future.successful(repo.remove(id))
}
