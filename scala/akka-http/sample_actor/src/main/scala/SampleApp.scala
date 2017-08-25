
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._
import scala.io.StdIn

case class RequestData(value: String, proc: String => Unit)

class RequestHandler extends Actor {
  override def receive: Receive = {
    case RequestData(v, p) => p(s"sample1: ${v}")
    case v: String => sender ! s"sample2: ${v}"
  }
}

object SampleApp extends App {
  implicit val system = ActorSystem("sample")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val actor = system.actorOf(Props[RequestHandler])

  val route = get {
    path("sample1") {
      parameter("v") { v =>
        completeWith(implicitly[ToResponseMarshaller[String]]) { f =>
          actor ! RequestData(v, f)
        }
      }
    } ~
    path("sample2" / Segment) { v: String =>
      implicit val timeout = Timeout(1 seconds)

      onSuccess(actor ? v) { r =>
        complete(s"result ${r}")
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println("started server ...")

  StdIn.readLine()

  bindingFuture.flatMap(_.unbind).onComplete(_ => system.terminate())
}
