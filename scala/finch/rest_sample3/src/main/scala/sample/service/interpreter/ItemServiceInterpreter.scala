package sample.service.interpreter

import java.util.UUID

import cats.data.Kleisli

import sample.model.{Amount, Item, ItemId, now}
import sample.repository.ItemRepository
import sample.service.ItemService

import scala.concurrent.Future
import scala.util.Try

object ItemServiceInterpreter extends ItemService {
  implicit def toFuture[A](t: Try[A]): Future[A] = Future.fromTry(t)

  private def itemOp[A](f: ItemRepository => Future[A]): ItemOp[A] = Kleisli(f)

  override def createItem(name: String, price: Amount): ItemOp[Item] =
    itemOp( _.store(Item(UUID.randomUUID().toString, name, price, now)) )

  override def getItem(id: ItemId): ItemOp[Option[Item]] =
    itemOp( _.restore(id) )

  override def deleteItem(id: ItemId): ItemOp[Option[Item]] =
    itemOp( _.delete(id) )
}
