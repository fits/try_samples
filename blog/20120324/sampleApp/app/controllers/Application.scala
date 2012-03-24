package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
	def index = Action {
		val samplePluginName = Play.current.plugin[plugins.SamplePlugin].map(_.name).getOrElse("nothing")

		Ok(views.html.index("plugin: " + samplePluginName))
	}
}