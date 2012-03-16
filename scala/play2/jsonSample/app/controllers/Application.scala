package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Application extends Controller {
  
	def index = Action {
		Ok(views.html.index("Your new application is ready."))
	}

	def json = Action {
		Ok(Json.toJson {
			Map(
				"id" -> "1",
				"name" -> "test"
			)
		})
	}

}