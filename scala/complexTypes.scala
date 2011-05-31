trait A {
	val name: String
	def test1 = println("test1: " + name)
}

trait B {
	val name: String
	def test2 = println("test2: " + name)
}

val ab = new A with B {
	val name = "a"
}

ab.test1
ab.test2
