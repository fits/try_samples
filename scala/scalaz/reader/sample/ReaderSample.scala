package fits.sample

import scalaz._
import Scalaz._

object ReaderSample extends App {

	val f = for {
		a <- Reader( (_: Int) * 2 )
		b <- Reader( (_: Int) + 10 )
		c <- Reader( (_: Int) - 5 )
	} yield a + b + c

	println(f(3))
}
