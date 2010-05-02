import scala.continuations.ControlContext._

def foo = {
	println("start foo")
	val res = shift {k: (Int => Int) => 
		println("shift start")
		k(k(k(1)))
	} + 5
	println("end foo = " + res)
	res
}

def bar = {
	println("start bar")
	
	val res = foo * 2
	println("end bar = " + res)
	res
}

println("result = " + reset(bar))

