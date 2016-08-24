
import akka.actor.{ActorSystem, Props}
import sample.{CounterAdd, SampleActor}

import scala.concurrent.Await
import scala.concurrent.duration._

object SampleApp extends App {
  val system = ActorSystem("sample1")
  val actor = system.actorOf(Props[SampleActor])

  actor ! "dump"

  actor ! CounterAdd(1)

  actor ! "dump"

  actor ! "snapshot"

  actor ! CounterAdd(3)

  actor ! "dump"

  actor ! "end"

  implicit val exeContext = system.dispatcher

  system.scheduler.scheduleOnce(10 seconds) {
    system.terminate
  }
}
