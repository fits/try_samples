package fits.sample

import scalaz._
import Scalaz._

object ReaderSample extends App {

	val plus3: Int => Int = 3 +
	val plus10: Int => Int = 7 +
	val times2: Int => Int = 2 *

	val f = for {
		a <- plus3
		b <- plus10
		c <- times2
	} yield a + b + c

	println(f(4))
}
