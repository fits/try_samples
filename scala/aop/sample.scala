
trait Simple {
	def action
}

trait BeforeSimple extends Simple {
	abstract override def action = {
		println("before")
		super.action
	}
}

class Tester extends Simple {
	def action = println("hello")
}

val t = new Tester with BeforeSimple
t.action
