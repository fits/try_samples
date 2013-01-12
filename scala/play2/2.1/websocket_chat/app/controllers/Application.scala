package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._

object Application extends Controller {

	val (enumerator, channel) = Concurrent.broadcast[JsValue]

	def connect = WebSocket.using[JsValue] { req =>

		val in = Iteratee.foreach[JsValue] { json =>
			channel.push(json)
		}.mapDone { _ => 
			println("*** closed")
		}

		(in, enumerator)
	}
}