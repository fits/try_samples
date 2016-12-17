
import akka.actor.Status.Failure
import akka.actor.{Actor, ActorSystem, Props}
import eventstore.{EventStream, ReadStreamEvents, ReadStreamEventsCompleted}
import eventstore.tcp.ConnectionActor

object SampleApp extends App {
  lazy val system = ActorSystem()

  lazy val connection = system.actorOf(ConnectionActor.props())
  implicit val receiver = system.actorOf(Props[ReceiveActor])

  connection ! ReadStreamEvents(EventStream.Id("sample1"))

  class ReceiveActor extends Actor {
    override def receive: Receive = {
      case ReadStreamEventsCompleted(events, next, last, end, _, _) =>
        println(s"next: ${next}, last: ${last}, isEnd: ${end}")

        events.foreach(ev => println(s"event: ${ev}"))

        context.system.terminate()

      case Failure(ex) =>
        println(s"error: ${ex}")
        context.system.terminate()
    }
  }
}
