package sample.app

import sample.service.{InventoryInterpreter, InventoryService}

object SampleApp extends App {
  val proc1 = for {
    id1 <- InventoryService.create("id-1")
    id2 <- InventoryService.create("id-2")
    _ <- InventoryService.add(id1, 5)
    _ <- InventoryService.add(id2, 10)
    _ <- InventoryService.remove(id1, 2)
    _ <- InventoryService.move(id2, id1, 1)
  } yield ()

  val r = InventoryInterpreter.interpret(proc1)

  println( r.run(Map.empty).value )
}
