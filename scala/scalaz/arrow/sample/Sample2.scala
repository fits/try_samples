package fits.sample

import scalaz._
import Scalaz._

object Sample2 extends App {

	val plus: Int => Int = 3 +
	val times: Int => Int = 2 * 

	// (7, 9) = (3 + 4, 2 * 4 + 1)
	println(4 |> plus &&& times >>^ (_ + 1))

	// (8, 10) = (3 + (4 + 1), 2 * (4 + 1))
	println(4 |> plus &&& times ^>> (_ + 1))

}
