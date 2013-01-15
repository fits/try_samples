package fits.sample

import com.typesafe.play.mini._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._
import scala.concurrent.stm._

object SampleApp extends Application {

	val wsList = Ref(List[PushEnumerator[JsValue]]())

	def route = Routes({
		case GET(Path("/connect")) => WebSocket.using[JsValue] { req =>
			val out = Enumerator.imperative[JsValue]()

			val in = Iteratee.foreach[JsValue] { json =>
				wsList.single.get.foreach( _.push(json) )
			}.mapDone { _ => 
				wsList.single.transform {
					_.filterNot( _ == out )
				}
				println("*** closed")
			}

			wsList.single.transform( _ :+ out )

			(in, out)
		}
	})
}
