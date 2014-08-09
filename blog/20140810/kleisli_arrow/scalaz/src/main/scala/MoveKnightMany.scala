package sample

import scalaz._
import Scalaz._

object MoveKnightMany extends App {

	type KnightPos = Tuple2[Int, Int]

	val inRange = (p: Int) => 1 to 8 contains p

	val moveKnight = (p: KnightPos) => List(
		(p._1 + 2, p._2 - 1), (p._1 + 2, p._2 + 1),
		(p._1 - 2, p._2 - 1), (p._1 - 2, p._2 + 1),
		(p._1 + 1, p._2 - 2), (p._1 + 1, p._2 + 2),
		(p._1 - 1, p._2 - 2), (p._1 - 1, p._2 + 2)
	).filter { case (x, y) => inRange(x) && inRange(y) }

	val inMany = (x: Int) => List.fill(x) { Kleisli(moveKnight) }.reduce { (a, b) => a >>> b }
	// 以下でも可
	// val inMany = (x: Int) => List.fill(x) { Kleisli(moveKnight) }.reduce { (a, b) => a >=> b }

	val canReachInMany = (x: Int) => (end: KnightPos) => inMany(x).run >>> { xs => xs.contains(end) }

	(6, 2) |> inMany(3) |> println

	(6, 2) |> canReachInMany(3)(6, 1) |> println
	(6, 2) |> canReachInMany(3)(7, 3) |> println
}
