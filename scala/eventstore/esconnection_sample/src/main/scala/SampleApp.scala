
import akka.actor.ActorSystem
import eventstore.{Content, EsConnection, EventData, EventStream, ReadStreamEvents, WriteEvents}

import scala.util.{Failure, Success}

object SampleApp extends App {
  lazy val system = ActorSystem()

  import system.dispatcher
  //implicit val ectx = system.dispatcher

  val con = EsConnection(system)

  val streamId = EventStream.Id("sample1")

  val event = EventData("sample-event", data = Content("sample data"))

  val res = for {
    w <- con.apply(WriteEvents(streamId, List(event)))
    r <- con.apply(ReadStreamEvents(streamId))
  } yield (w, r)

  res.onComplete {
      case Success((w, r)) =>
        println(s"write result: ${w}")
        println(s"read result next: ${r.nextEventNumber}, last: ${r.lastEventNumber}, isEnd: ${r.endOfStream}")
        r.events.foreach(ev => println(s"event: ${ev}"))
        system.terminate()

      case Failure(ex) =>
        println(s"error: ${ex}")
        system.terminate()
  }
}
