package sample.app

object SampleApp extends App {
  val id1 = "id-1"
  val id2 = "id-2"

  import cats.implicits._
  import sample.repository.interpreter.MemoryInventoryRepository
  import sample.service.interpreter.InventoryService

  val proc1 = for {
    _ <- InventoryService.add(id1, 5)
    v <- InventoryService.move(id1, id2, 4)
  } yield v

  val r1 = proc1(MemoryInventoryRepository)
  println(r1)

  println( InventoryService.move(id1, id2, 2).run(MemoryInventoryRepository) )

  val proc2 = for {
    v1 <- InventoryService.current(id1)
    v2 <- InventoryService.current(id2)
  } yield (v1, v2)

  println( proc2(MemoryInventoryRepository) )
}
