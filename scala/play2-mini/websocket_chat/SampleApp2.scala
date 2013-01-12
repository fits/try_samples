package fits.sample

import com.typesafe.play.mini._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.libs.iteratee._

import scala.collection.JavaConversions._

object SampleApp2 extends Application {

	val wsList = new java.util.concurrent.CopyOnWriteArrayList[PushEnumerator[JsValue]]()

	def route = Routes({
		case GET(Path("/connect")) => WebSocket.using[JsValue] { req =>
			val out = Enumerator.imperative[JsValue]()

			wsList.add(out)

			val in = Iteratee.foreach[JsValue] { json =>
				wsList.foreach( _.push(json) )
			}.mapDone { _ => 
				wsList.remove(out)
				println("*** closed")
			}

			(in, out)
		}
	})
}
