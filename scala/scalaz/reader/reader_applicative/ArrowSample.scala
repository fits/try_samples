package fits.sample

import scalaz._
import Scalaz._

object ArrowSample extends App {

	val plus3: Int => Int = 3 +
	val plus10: Int => Int = 7 +
	val times2: Int => Int = 2 *

	val f0 = plus3 &&& plus10 &&& times2
	// ((7, 11), 8)
	println(f0(4))

	val f1 = (plus3 &&& plus10 &&& times2) >>> { case ((a, b), c) => a + b + c }
	// 26
	println(f1(4))

	// 26
	println(4 |> plus3 &&& plus10 &&& times2 |> { case ((a, b), c) => a + b + c })
}
