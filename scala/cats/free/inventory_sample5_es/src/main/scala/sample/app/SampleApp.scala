package sample.app

import sample.service.{EventStoreInterpreter, InventoryInterpreter, InventoryService}

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

  val er1 = EventStoreInterpreter.interpret(proc1)

  println(er1.value)
  println(EventStoreInterpreter.restore("id-1"))
  println(EventStoreInterpreter.restore("id-2"))

  val proc2 = for {
    _ <- InventoryService.remove("id-1", 2)
    _ <- InventoryService.remove("id-1", 3)
  } yield ()

  val er2 = EventStoreInterpreter.interpret(proc2)

  println(er2.value)
  println(EventStoreInterpreter.restore("id-1"))

  val er3 = EventStoreInterpreter.interpret(InventoryService.move("id-1", "id-2", 3))

  println(er3.value)
  println(EventStoreInterpreter.restore("id-1"))
  println(EventStoreInterpreter.restore("id-2"))
}
