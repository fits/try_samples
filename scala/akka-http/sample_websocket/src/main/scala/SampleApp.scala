
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object SampleApp extends App {
  implicit val system: ActorSystem = ActorSystem("sample")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val websocketFlow = { name: String =>
    Flow[Message].collect {
      case TextMessage.Strict(msg) =>
        println(s"*** receive $name: $msg")
        TextMessage.Strict(s"$name: $msg")
    }
  }

  val route = get {
    pathSingleSlash {
      getFromResource("web/index.html")
    } ~
    path("sample") {
      parameter("name") { name =>
        handleWebSocketMessages(websocketFlow(name))
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println("started server ...")

  StdIn.readLine()

  bindingFuture.flatMap(_.unbind).onComplete(_ => system.terminate())
}
