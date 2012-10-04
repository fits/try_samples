package fits.sample

import scalaz._
import Scalaz._

object Sample extends App {

	val plus: Int => Int = 3 +
	val times: Int => Int = 2 * 

	// 14
	println(4 |> plus >>> times)

	// 11
	println(4 |> plus <<< times)

	// 34
	println(4 |> plus >>> times >>> plus >>> times)

}
