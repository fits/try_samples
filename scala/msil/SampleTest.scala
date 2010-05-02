
class SampleNode(val name: String) {
	def printName() = println(name)
}

object SampleTest extends Application {

	override def main(args: Array[String]) {
		new SampleNode("abc").printName()
	}
}

