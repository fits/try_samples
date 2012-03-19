package plugins

import play.api._

object Sample {
	def getPlugin(implicit app: Application): Option[SamplePlugin] = {
		app.plugin[SamplePlugin]
	}
}

class SamplePlugin(app: Application) extends Plugin {

	override def onStart() {
		println("--- sample plugin start")
	}

	override def onStop() {
		println("--- sample plugin stop")
	}
}

