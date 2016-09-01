package sample

import akka.actor.Actor

class SubActor extends Actor {

  override def receive: Receive = {
    case d => println(s"** child receive ${self}: ${d}")
  }
}