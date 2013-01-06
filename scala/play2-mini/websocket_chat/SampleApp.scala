package fits.sample

import com.typesafe.play.mini._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.libs.iteratee._

object SampleApp extends Application {

	var wsList = List[PushEnumerator[JsValue]]()

	def route = Routes({
		case GET(Path("/connect")) => WebSocket.using[JsValue] { req =>
			val out = Enumerator.imperative[JsValue]()

			wsList = wsList :+ out

			val in = Iteratee.foreach[JsValue] { json =>
				wsList.foreach( _.push(json) )
			}.mapDone { _ => 
				wsList = wsList.filterNot { _ == out }
				println("*** closed")
			}

			(in, out)
		}
	})
}
