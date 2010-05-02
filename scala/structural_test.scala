class A (val name: String) {
	def test = println("A_test:" + name)
}

class B {
	def test = println("B_test")
}

def callTest(target: {def test: Unit}) = target test

callTest(new A("a"))
callTest(new B)
callTest(new {def test = println("anonymous")})
