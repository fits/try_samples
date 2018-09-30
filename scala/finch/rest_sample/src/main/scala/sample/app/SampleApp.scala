package sample.app

import java.util.UUID

import com.twitter.finagle.Http
import com.twitter.util.Await

import io.finch._
import io.finch.syntax._
import io.finch.circe._
import io.circe.generic.auto._

import sample.model.{Amount, Item, ItemId, now}

import scala.collection.mutable

case class ItemInput(name: String, price: Amount)

object SampleApp extends App {
  private lazy val repo = mutable.Map.empty[ItemId, Item]

  private def toOutput[A](d: Option[A]): Output[A] =
    d.map(Ok).getOrElse(NotFound(new Exception))

  val itemGet = get("items" :: path[ItemId]) { id: ItemId =>
    toOutput(repo.get(id))
  }

  val itemDelete: Endpoint[Item] = delete("items" :: path[ItemId]) { id: ItemId =>
    toOutput(repo.remove(id))
  }

  val itemPost = post("items" :: jsonBody[ItemInput]) { d: ItemInput =>
    val item = Item(UUID.randomUUID().toString, d.name, d.price, now)

    repo += ((item.id, item))

    Created(item)
  }

  val api = (itemGet :+: itemPost :+: itemDelete).toServiceAs[Application.Json]

  val server = Http.server.serve(":8080", api)

  Await.ready(server)
}
