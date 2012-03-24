package plugins

import play.api._

class SamplePlugin(app: Application) extends Plugin {
	val name = "sample plugin"

	override def onStart() {
		println("--- " + name + " start")
	}

	override def onStop() {
		println("--- " + name + " stop")
	}
}

