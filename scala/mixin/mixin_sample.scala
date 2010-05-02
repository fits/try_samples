
trait Printer {
	def name(): String

	def print() {
		println(this + ", " + name())
	}
}

trait Owner {
	def name(): String = "abc"
}

class Test extends Printer with Owner {
}

var t = new Test()
t.print()
