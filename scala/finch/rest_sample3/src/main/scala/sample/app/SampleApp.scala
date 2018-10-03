package sample.app

import com.twitter.finagle.Http
import com.twitter.util.Await
import io.finch._
import io.finch.syntax._
import io.finch.syntax.scalaFutures._
import io.finch.circe._
import io.circe.generic.auto._
import sample.model.{Amount, ItemId}
import sample.repository.interpreter.{DbItemRepositoryInterpreter, ItemRepositoryInterpreter}
import sample.service.interpreter.ItemServiceInterpreter.{createItem, deleteItem, getItem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

case class ItemInput(name: String, price: Amount)

object SampleApp extends App {
  val repo = Properties.envOrNone("SAMPLE_APP_MODE")
    .filter(_ == "PROD")
    .map(_ => DbItemRepositoryInterpreter)
    .getOrElse(ItemRepositoryInterpreter)

  println(repo)

  private def toOutput[A](opt: Option[A]): Output[A] =
    opt.map(Ok).getOrElse(NotFound(new Exception))

  val itemGet = get("items" :: path[ItemId]) { id: ItemId =>
    getItem(id)(repo).map(toOutput)
  }

  val itemDelete = delete("items" :: path[ItemId]) { id: ItemId =>
    deleteItem(id)(repo).map(toOutput)
  }

  val itemPost = post("items" :: jsonBody[ItemInput]) { d: ItemInput =>
    createItem(d.name, d.price)(repo).map(Created)
  }

  val api = (itemGet :+: itemPost :+: itemDelete).toServiceAs[Application.Json]

  val server = Http.server.serve(":8080", api)

  Await.ready(server)
}
