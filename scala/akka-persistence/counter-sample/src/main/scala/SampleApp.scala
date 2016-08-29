
import akka.actor.{ActorSystem, Props}
import akka.pattern._

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

  implicit val exeContext = system.dispatcher

  gracefulStop(actor, 5 seconds, "end").onComplete(_ => system.terminate)
}
