package fits.sample

import scalaz._
import Scalaz._

object LengthCompare extends App {

	val lengthCompare = (x: String, y: String) => (x.length ?|? y.length) |+| (x ?|? y)
	//以下でも同じ
	//val lengthCompare = (x: String, y: String) => (x.length ?|? y.length) mappend (x ?|? y)

	println(lengthCompare("zen", "ants"))
	println(lengthCompare("zen", "ant"))

	println("-------------")

	val lengthCompare2 = (x: String, y: String) => Ordering.fromInt(x.length compare y.length) mappend Ordering.fromInt(x compare y)

	println(lengthCompare2("zen", "ants"))
	println(lengthCompare2("zen", "ant"))

	println("-------------")

	val lengthCompare3 = (x: String, y: String) => Order[Int].order(x.length, y.length) mappend Order[String].order(x, y)

	println(lengthCompare3("zen", "ants"))
	println(lengthCompare3("zen", "ant"))

	println("-------------")

	val lengthCompare4 = (x: String, y: String) => intInstance(x.length, y.length) mappend stringInstance(x, y)

	println(lengthCompare4("zen", "ants"))
	println(lengthCompare4("zen", "ant"))
}
