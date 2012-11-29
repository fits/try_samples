package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Application extends Controller {

	def index = Action {
		Ok(views.html.index())
	}

	def send = Action { req =>
		println("--- query string ---")
		println(req.queryString)
		
		println("--- json ---")
		println(req.body.asJson)
		
		Ok("")
	}

}