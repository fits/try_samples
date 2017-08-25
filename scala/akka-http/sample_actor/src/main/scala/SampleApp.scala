
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn

case class RequestData(value: String, proc: String => Unit)

class RequestHandler extends Actor {
  override def receive: Receive = {
    case RequestData(v, p) => p(s"ok: ${v}")
  }
}

object SampleApp extends App {
  implicit val system = ActorSystem("sample")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val actor = system.actorOf(Props[RequestHandler])

  val route = get {
    path("sample") {
      parameter("v") { v =>
        completeWith(implicitly[ToResponseMarshaller[String]]) { f =>
          actor ! RequestData(v, f)
        }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println("started server ...")

  StdIn.readLine()

  bindingFuture.flatMap(_.unbind).onComplete(_ => system.terminate())
}
