package sample

import scalaz._
import Scalaz._

object SampleApp extends App {
	val plus2 = 2 + (_: Int)
	val times10 = 10 * (_: Int)

	// 7
	5 |> plus2 |> println

	// 70
	5 |> plus2 >>> times10 |> println

	// 70
	5 |> plus2 >>^ times10 |> println

	// (7, 3)
	(5, 3) |> plus2.first |> println

	// (5, 30)
	(5, 3) |> times10.second |> println

	// (50, 30)
	(5, 3) |> times10.product |> println

	// (7, 30)
	(5, 3) |> plus2.first |> times10.second |> println

	// (7, 30)
	(5, 3) |> plus2 *** times10 |> println

	// (7, 30)
	(5, 3) |> plus2 -*- times10 |> println

	// (7, 50)
	5 |> plus2 &&& times10 |> println
}
