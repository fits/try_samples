package fits.sample

import scala.language.postfixOps

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

	// (6, 10)
	println( (plus *** times)(3, 5) )
	// (6, 10)
	println( (3, 5) |> plus *** times )

	// (5, 4)
	println( (plus &&& times)(2) )
	// (5, 4)
	println(2 |> plus &&& times )

}
