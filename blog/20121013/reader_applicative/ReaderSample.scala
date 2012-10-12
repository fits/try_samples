package fits.sample

import scalaz._
import Scalaz._

object ReaderSample extends App {

	val f1 = for {
		a <- 2 * (_: Int)
		b <- 10 + (_: Int)
	} yield a + b

	println(f1(4))

	val f2 = for {
		a <- 2 * (_: Int)
		b <- 10 + (_: Int)
		c <- 5 + (_: Int)
	} yield a + b + c

	println(f2(4))
}
