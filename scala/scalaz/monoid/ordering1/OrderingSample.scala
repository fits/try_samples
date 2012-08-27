package fits.sample

import scalaz._
import Scalaz._

object OrderingSample extends App {

	val lenCompare = (x: String, y: String) => Ordering.fromInt(x.length compare y.length) mappend Ordering.fromInt(x compare y)

	println(lenCompare("aa", "ab"))
	println(lenCompare("bb", "c"))
	println(lenCompare("cc00", "bb12"))
	println(lenCompare("a", "a"))

}
