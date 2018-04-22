package sample.app

import scala.concurrent.Await
import scala.concurrent.duration._

object SampleApp extends App {
  val id1 = "id-1"
  val id2 = "id-2"

  import scala.concurrent.ExecutionContext.Implicits.global
  import sample.service.interpreter.InventoryService
  import sample.repository.interpreter.MemoryInventoryOpInterpreter._

  val proc1 = for {
    _ <- InventoryService.add(id1, 5)
    v <- InventoryService.move(id1, id2, 4)
  } yield v

  val r1 = interpret(proc1)
  r1.foreach(println)

  Await.ready(r1, 5.seconds)

  val r2 = interpret(InventoryService.move(id1, id2, 2))
  r2.map(_.toString).recover{ case e => e.getMessage }.foreach(println)

  Await.ready(r2, 5.seconds)

  val proc3 = for {
    v1 <- InventoryService.current(id1)
    v2 <- InventoryService.current(id2)
  } yield (v1, v2)

  val r3 = interpret(proc3)
  r3.foreach(println)

  Await.ready(r3, 5.seconds)
}
