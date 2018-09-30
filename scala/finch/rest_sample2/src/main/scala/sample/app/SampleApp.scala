package sample.app

import com.twitter.finagle.Http
import com.twitter.util.Await

import io.finch._
import io.finch.syntax._
import io.finch.syntax.scalaFutures._
import io.finch.circe._
import io.circe.generic.auto._

import sample.model.{Amount, Item, ItemId}
import sample.service.ItemService.{createItem, getItem, deleteItem}

import scala.concurrent.ExecutionContext.Implicits.global

case class ItemInput(name: String, price: Amount)

object SampleApp extends App {
  val itemGet = get("items" :: path[ItemId]) { id: ItemId =>
    getItem(id).map(Ok)
  }

  val itemDelete = delete("items" :: path[ItemId]) { id: ItemId =>
    deleteItem(id).map(Ok)
  }

  val itemPost = post("items" :: jsonBody[ItemInput]) { d: ItemInput =>
    createItem(d.name, d.price).map(Created)
  }

  val api = (itemGet :+: itemPost :+: itemDelete).toServiceAs[Application.Json]

  val server = Http.server.serve(":8080", api)

  Await.ready(server)
}
