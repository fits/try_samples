package sample

import scalaz._
import Scalaz._

object MoveKnight extends App {

	type KnightPos = Tuple2[Int, Int]

	val inRange = (p: Int) => 1 to 8 contains p

	val moveKnight = (p: KnightPos) => List(
		(p._1 + 2, p._2 - 1), (p._1 + 2, p._2 + 1),
		(p._1 - 2, p._2 - 1), (p._1 - 2, p._2 + 1),
		(p._1 + 1, p._2 - 2), (p._1 + 1, p._2 + 2),
		(p._1 - 1, p._2 - 2), (p._1 - 1, p._2 + 2)
	).filter { case (x, y) => inRange(x) && inRange(y) }

	val in3 = Kleisli(moveKnight) >==> moveKnight >==> moveKnight

	in3 (6, 2) |> println

	val canReachIn3 = (end: KnightPos) => in3.run >>> { xs => xs.contains(end) }

	(6, 2) |> canReachIn3 (6, 1) |> println
	(6, 2) |> canReachIn3 (7, 3) |> println
}
