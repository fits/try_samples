package fits.sample

import scalaz._
import Scalaz._

object ReaderSample extends App {
	val multiply = (x: Int) => (y: Int) => x * y
	val plus = (x: Int) => (y: Int) => x + y

	val f1 = for {
		a <- multiply(2)
		b <- plus(10)
	} yield a + b

	println(f1(4))

	val f2 = for {
		a <- multiply(2)
		b <- plus(10)
		c <- plus(5)
	} yield a + b + c

	println(f2(4))
}
