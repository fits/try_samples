import rx._

object SampleApp extends App {

	val a = Var(1)
	val b = Var(2)

	val c = Rx { a() + b() }
	val d = Rx { a() * c() }

	println(s"c = ${c()}")
	println(s"d = ${d()}")

	a() = 3

	println(s"c = ${c()}")
	println(s"d = ${d()}")
}