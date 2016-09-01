
import akka.actor.{ActorSystem, Props}
import akka.pattern._
import sample._

import scala.language.postfixOps
import scala.concurrent.duration._

object SampleApp extends App {
  val system = ActorSystem("s1")
  implicit val exeContext = system.dispatcher

  val actor = system.actorOf(Props[SampleActor], "pa1")

  println(actor.path)

  actor ! AddActor("ch1")
  actor ! AddActor("ch2")
  actor ! "debug"

  println(system.actorOf(Props[SampleActor]).path)

  system.actorSelection("user/*") ! "debug"

  gracefulStop(actor, 5 seconds, "stop")
    .onComplete(_ => system.terminate())
}
