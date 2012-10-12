package fits.sample

import scalaz._
import Scalaz._

object ReaderSample extends App {
	val multiply = (x: Int) => (y: Int) => x * y
	val plus = (x: Int) => (y: Int) => x + y

	val f1 = for {
		a <- Reader(multiply(2))
		b <- Reader(plus(10))
	} yield a + b

	println(f1(4))

	val f2 = for {
		a <- Reader(multiply(2))
		b <- Reader(plus(10))
		c <- Reader(plus(5))
	} yield a + b + c

	println(f2(4))
}
