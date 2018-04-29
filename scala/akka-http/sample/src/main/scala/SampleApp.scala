
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object SampleApp extends App {
  implicit val system: ActorSystem = ActorSystem("sample1")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val route = get {
    complete("sample")
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println("started server ...")

  StdIn.readLine()

  bindingFuture.flatMap(_.unbind).onComplete(_ => system.terminate())
}
