
def testFunc = {
	var x = 1
	val y = {
		println("init y")
		x + 10
	}

	x = 2

	println("---")
	println("x = " + x + ", y = " + y)
}

def lazyTestFunc = {
	var x = 1
	lazy val y = {
		println("init y")
		x + 10
	}

	x = 2

	println("---")
	println("x = " + x + ", y = " + y)
}

testFunc

println()

lazyTestFunc

