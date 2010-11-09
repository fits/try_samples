import scala.swing._

object SampleApp extends SimpleGUIApplication {

	def top = new MainFrame {
		title = "テスト"
		val button = new Button {
			text = "push"
		}

		contents = new BoxPanel(Orientation.Vertical) {
			contents += button
		}
	}

}
