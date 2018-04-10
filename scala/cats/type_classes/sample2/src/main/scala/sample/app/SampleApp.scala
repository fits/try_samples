package sample.app

object SampleApp extends App {
  val id1 = "id-1"
  val id2 = "id-2"

  import sample.service.interpreter.InventoryService

  println(InventoryService.add(id1, 5))
  println(InventoryService.move(id1, id2, 4))
  println(InventoryService.move(id1, id2, 2))
  println(s"1: ${InventoryService.current(id1)}, 2: ${InventoryService.current(id2)}")

  println("-----")

  import sample.service.interpreter.InventoryService2
  import sample.service.interpreter.InventoryService2._

  println(InventoryService2.add(id1, 5))
  println(InventoryService2.move(id1, id2, 4))
  println(InventoryService2.move(id1, id2, 2))
  println(s"1: ${InventoryService2.current(id1)}, 2: ${InventoryService2.current(id2)}")

  println("-----")

  import sample.service.interpreter.InventoryService3

  println(InventoryService3.add(id1, 5))
  println(InventoryService3.move(id1, id2, 4))
  println(InventoryService3.move(id1, id2, 2))
  println(s"1: ${InventoryService3.current(id1)}, 2: ${InventoryService3.current(id2)}")
}
