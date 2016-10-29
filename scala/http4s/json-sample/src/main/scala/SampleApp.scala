
import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.json4s.jackson._
import org.json4s.{DefaultFormats, JValue, Reader}

import scala.io.StdIn

case class Data(name: String, value: Int)

object SampleApp extends App {

  implicit val formats = DefaultFormats
  implicit val dataReader = new Reader[Data] {
    override def read(value: JValue): Data = value.extract[Data]
  }
  implicit val jsonDec = jsonOf[Data]

  val sampleService = HttpService {
    case req @ POST -> Root / "json" =>
      req.as[Data].flatMap { d =>
        println(s"json = ${d}")
        Ok(s"ok: ${d}")
      }
  }

  val server = BlazeBuilder.bindLocal(8080)
    .mountService(sampleService)
    .run

  println("press enter key to quit")

  StdIn.readLine()

  server.shutdownNow()
}
