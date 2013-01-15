package fits.sample

import com.typesafe.play.mini._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._

object SampleApp extends Application {

	val (enumerator, channel) = Concurrent.broadcast[JsValue]

	def route = Routes({
		case GET(Path("/connect")) => WebSocket.using[JsValue] { req =>
			val in = Iteratee.foreach[JsValue] { json =>
				channel.push(json)
			}.mapDone { _ => 
				println("*** closed")
			}

			(in, enumerator)
		}
	})
}
