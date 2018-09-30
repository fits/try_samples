package sample.service

import java.util.UUID

import sample.model.{Amount, Item, ItemId, now}

import scala.collection.mutable
import scala.concurrent.Future

object ItemService {
  private lazy val repo = mutable.Map.empty[ItemId, Item]

  implicit def toFuture[A](opt: Option[A]): Future[A] =
    opt.map(Future.successful).getOrElse(Future.failed(new Error()))

  def createItem(name: String, price: Amount): Future[Item] = {
    val item = Item(UUID.randomUUID().toString, name, price, now)

    repo += ((item.id, item))

    Future.successful(item)
  }

  def getItem(id: ItemId): Future[Item] = repo.get(id)

  def deleteItem(id: ItemId): Future[Item] = repo.remove(id)
}
