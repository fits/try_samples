import scala.continuations.ControlContext._

val res = reset {
	println("before shift")

	val a = shift {k: (Int => Int) =>
		println("before k ")
		val i = k(k(1))
		println("i = " + i)
		i
	} +1
	
	println("after shift : a = " + a)
	
	a * 2
}

println(res)
