package fits.sample

import scalaz._
import Scalaz._
import WriterT._

object MoveKnightWriter extends App {

	type KnightPos = Tuple2[Int, Int]
	type PosCalc = Function2[Int, Int, Int]

	val createWriter = (p: KnightPos, sep: String) => writer (s"$sep$p", p)

	val nextPos = (a: Int, b: Int, fa: PosCalc, fb: PosCalc) => 
		(p: KnightPos) => createWriter((fa(p._1, a), fb(p._2, b)), " -> ")

	val fl = List((_: Int) + (_: Int), (_: Int) - (_: Int))

	val moveKnight = (p: Writer[String, KnightPos]) => 
		(
			for {
				a <- List(2, 1)
				b <- List(2, 1)
				fa <- fl
				fb <- fl
				if a != b
			} yield p >>= nextPos(a, b, fa, fb)
		) filter {
			_.value match {
				case (x, y) => 1 <= x && x <= 8 && 1 <= y && y <= 8
			}
		}

	val inMany = (x: Int) => (start: KnightPos) => {
		List(createWriter(start, "")) >>= List.fill(x){ Kleisli(moveKnight) }.reduceRight {(a, b) =>
			b <=< a
		}
	}

	val routeReachIn = (x: Int) => (start: KnightPos, end: KnightPos) =>
		inMany(x)(start).filter { _.value == end } map { _.written }

	routeReachIn(3)((6, 2), (6, 1)).foreach {println}
	println("-----")
	routeReachIn(3)((6, 2), (7, 3)).foreach {println}
}
