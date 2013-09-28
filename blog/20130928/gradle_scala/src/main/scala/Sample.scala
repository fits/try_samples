package fits.sample

import scala.language.postfixOps

import scalaz._
import Scalaz._

object Sample extends App {

	val plus3: Int => Int = 3 +
	val times: Int => Int = 2 *

	// 2 * (3 + 4) = 14
	println( 4 |> plus3 >>> times )

	// (3 + 4, 2 * 5) = (7, 10)
	println( (4, 5) |> plus3 *** times )

	// (3 + 5) + (2 * 5) = 18
	println( 5 |> plus3 &&& times )
}
