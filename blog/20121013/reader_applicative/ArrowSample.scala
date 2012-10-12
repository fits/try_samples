package fits.sample

import scalaz._
import Scalaz._

object ArrowSample extends App {
	val multiply = (x: Int) => (y: Int) => x * y
	val plus = (x: Int) => (y: Int) => x + y

	val f1 = ( multiply(2) &&& plus(10) ) >>> { case (a, b) => a + b }
	println(f1(4))

	val f2 = ( multiply(2) &&& plus(10) &&& plus(5) ) >>> { case ((a, b), c) => a + b + c }
	println(f2(4))
}
