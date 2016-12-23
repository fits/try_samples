
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

import com.twitter.finagle.Http
import com.twitter.util.Await

case class Data(id: String, value: Int)

object SampleApp extends App {

  val api = get("sample" :: string) { id: String => Ok(Data(id, 1)) }

  val server = Http.server.serve(":8080", api.toServiceAs[Application.Json])

  Await.ready(server)
}
