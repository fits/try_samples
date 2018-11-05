package sample.app

import com.twitter.finagle.Http
import com.twitter.util.Await
import io.finch._
import io.finch.syntax._
import io.finch.syntax.scalaFutures._
import io.finch.circe._
import io.circe.generic.auto._
import sample.model._
import sample.repository.interpreter.MemoryStockRepository
import sample.service.interpreter.StockServiceInterpreter

import scala.concurrent.ExecutionContext.Implicits.global

case class CreateStock(itemId: ItemId, managed: Boolean)
case class MoveStock(from: StockId, to: StockId, qty: Quantity)

object SampleApp extends App {
  val repo = new MemoryStockRepository
  val service = new StockServiceInterpreter

  private def toOutput[A](opt: Option[A]): Output[A] =
    opt.map(Ok).getOrElse(NotFound(new Exception))

  val findStock = get("stocks" :: path[StockId]) { stockId: StockId =>
    service.current(stockId)
      .run(repo)
      .map(toOutput)
  }

  val registStock = post("stocks" :: jsonBody[CreateStock]) { param: CreateStock =>
    service.regist(param.itemId, param.managed)
      .run(repo)
      .map(Created)
  }

  val moveStock = put("stocks" :: jsonBody[MoveStock]) { param: MoveStock =>
    service.move(param.from, param.to, param.qty)
      .run(repo)
      .map(Ok)
  }

  val api = (findStock :+: registStock :+: moveStock).toServiceAs[Application.Json]

  val server = Http.server.serve(":8080", api)

  Await.ready(server)
}
