package sample.app

import scala.concurrent.Await
import scala.concurrent.duration._

object SampleApp extends App {
  val id1 = "id-1"
  val id2 = "id-2"

  import cats.implicits._
  import scala.concurrent.ExecutionContext.Implicits.global
  import sample.service.interpreter.InventoryService
  import sample.repository.interpreter.MemoryInventoryOpInterpreter._

  val proc1 = for {
    _ <- InventoryService.add(id1, 5)
    v <- InventoryService.move(id1, id2, 4)
  } yield v

  val proc3 = for {
    v1 <- InventoryService.current(id1)
    v2 <- InventoryService.current(id2)
  } yield (v1, v2)

  val r = for {
    r1 <- interpret(proc1)
    r2 <- interpret(InventoryService.move(id1, id2, 2))
    r3 <- interpret(proc3)
  } yield (r1, r2, r3)

  r.foreach { t =>
    println(t._1)
    println(t._2)
    println(t._3)
  }

  Await.ready(r, 10.seconds)
}
