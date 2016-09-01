package sample

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import akka.pattern._

import scala.language.postfixOps
import scala.collection.mutable.MutableList
import scala.concurrent.duration._
import scala.concurrent.Future._

case class AddActor(val name: String)

class SampleActor extends Actor {
  implicit lazy val exeContext = context.system.dispatcher

  override def receive: Receive = {
    case AddActor(name) => {
      val ch = context.actorOf(Props[SubActor], name)

      println(s"* add child - ${ch.path}")
   }
    case "debug" => {
      println(s"* debug ${self}")

      context.actorSelection("*") ! "test"
      context.children.foreach(act => act ! "test2")
    }
    case "stop" => {
      println("* stop")

      sequence(context.children.map(act => gracefulStop(act, 5 seconds)))
        .onComplete(_ => context.stop(self))
    }
  }
}