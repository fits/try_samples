package fits.sample

import scalaz._
import Scalaz._

object Sample extends App {

	type KnightPos = Tuple2[Int, Int]

	val moveKnight = (p: KnightPos) => List(
		(p._1 + 2, p._2 - 1), (p._1 + 2, p._2 + 1),
		(p._1 - 2, p._2 - 1), (p._1 - 2, p._2 + 1),
		(p._1 + 1, p._2 - 2), (p._1 + 1, p._2 + 2),
		(p._1 - 1, p._2 - 2), (p._1 - 1, p._2 + 2)
	).filter { case (x, y) => 1 <= x && x <= 8 && 1 <= y && y <= 8 }


	val inMany = (x: Int) => (start: KnightPos) => {
		List(start) >>= List.fill(x){ Kleisli(moveKnight) }.reduceRight {(a, b) =>
			b <=< a
		}
	}

	println(inMany(3)(6, 2))

	val canReachIn = (x: Int) => (start: KnightPos, end: KnightPos) =>
		inMany(x)(start).contains(end)

	println(canReachIn(3)((6, 2), (6, 1)))
	println(canReachIn(3)((6, 2), (7, 3)))
}
