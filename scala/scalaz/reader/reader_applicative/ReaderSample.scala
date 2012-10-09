package fits.sample

import scalaz._
import Scalaz._

object ReaderSample extends App {

	val plus3: Int => Int = 3 +
	val plus10: Int => Int = 7 +
	val times2: Int => Int = 2 *

	val f = for {
		a <- Reader(plus3)
		b <- Reader(plus10)
		c <- Reader(times2)
	} yield a + b + c

	println(f(4))
}
