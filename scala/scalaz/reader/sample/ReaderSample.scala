package fits.sample

import scalaz._
import Scalaz._

object ReaderSample extends App {

	val plus = (x: Int) => (y: Int) => x + y
	val multiply = (x: Int) => (y: Int) => x * y

	val f = for {
		a <- Reader(multiply(2))
		b <- Reader(plus(10))
	} yield a + b

	println(f(3))
}
