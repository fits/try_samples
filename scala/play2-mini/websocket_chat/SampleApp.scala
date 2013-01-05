package fits.sample

import com.typesafe.play.mini._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.libs.iteratee._

object SampleApp extends Application {

	def route = Routes({
		case GET(Path("/connect")) => WebSocket.using[JsValue] { req =>
			val out = Enumerator.imperative[JsValue]()

			val in = Iteratee.foreach[JsValue] { json =>
				out.push(json)
			}.mapDone { _ => println("*** closed") }

			(in, out)
		}
	})
}
