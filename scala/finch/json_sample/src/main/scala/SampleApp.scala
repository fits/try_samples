
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

import com.twitter.finagle.Http
import com.twitter.util.Await

case class Data(id: String, value: Int)

object SampleApp extends App {

  val dataGet = get("samples" :: string) { id: String => Ok(Data(id, 1)) }

  val dataPost = post("samples" :: jsonBody[Data]) { d: Data => 
    println(s"data = ${d}")
    Ok(d)
  }

  val api = dataGet :+: dataPost

  val server = Http.server.serve(":8080", api.toServiceAs[Application.Json])

  Await.ready(server)
}
