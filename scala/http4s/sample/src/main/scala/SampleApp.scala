import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder

import scala.io.StdIn

object SampleApp extends App {
  val sampleService = HttpService {
    case req @ GET -> Root / "sample" / id => Ok(s"sample: ${id}")
  }

  val server = BlazeBuilder.bindLocal(8080)
    .mountService(sampleService)
    .run

  println("press enter key to quit")

  StdIn.readLine()

  server.shutdownNow()
}
