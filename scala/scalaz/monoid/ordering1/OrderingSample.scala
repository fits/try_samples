package fits.sample

import scalaz._
import Scalaz._

object OrderingSample extends App {

	val lenCompare = (x: String, y: String) => Ordering.fromInt(x.length compare y.length) mappend Ordering.fromInt(x compare y)

	println(lenCompare("aa", "ab"))
	println(lenCompare("bb", "c"))
	println(lenCompare("cc00", "bb12"))
	println(lenCompare("a", "a"))

	println("-------------")

	val lenCompare2 = (x: String, y: String) => Order[Int].order(x.length, y.length) mappend Order[String].order(x, y)

	println(lenCompare2("aa", "ab"))
	println(lenCompare2("bb", "c"))
	println(lenCompare2("cc00", "bb12"))
	println(lenCompare2("a", "a"))

	println("-------------")

	val lenCompare3 = (x: String, y: String) => intInstance(x.length, y.length) mappend stringInstance(x, y)

	println(lenCompare3("aa", "ab"))
	println(lenCompare3("bb", "c"))
	println(lenCompare3("cc00", "bb12"))
	println(lenCompare3("a", "a"))


	println("-------------")

	val lenCompare4 = (x: String, y: String) => (x.length ?|? y.length) |+| (x ?|? y)

	println(lenCompare4("aa", "ab"))
	println(lenCompare4("bb", "c"))
	println(lenCompare4("cc00", "bb12"))
	println(lenCompare4("a", "a"))

}
