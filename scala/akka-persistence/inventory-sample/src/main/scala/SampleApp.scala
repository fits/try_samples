
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.pattern._

import sample.commands.{CheckInItemsToInventory, CreateInventoryItem}
import sample.InventoryActor

import scala.concurrent.duration._

object SampleApp extends App {
  val system = ActorSystem("sample1")
  implicit val exeContext = system.dispatcher

  val id = "s1"
  val actor = system.actorOf(Props(classOf[InventoryActor], id))

  actor ! "dump"

  actor ! CreateInventoryItem(id, "sample1")

  actor ! "dump"

  actor ! CheckInItemsToInventory(id, 5)

  actor ! "snapshot"

  actor ! "dump"

  actor ! CheckInItemsToInventory(id, 3)

  actor ! "dump"

  gracefulStop(actor, 5 seconds, "stop").onComplete(_ => system.terminate)
}
