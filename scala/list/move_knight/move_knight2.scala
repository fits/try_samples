
type KnightPos = Tuple2[Int, Int]

val fl = List((_: Int) - (_: Int), (_: Int) + (_: Int))
//以下でも可
// import scala.math.Numeric.IntIsIntegral
// val fl = List(IntIsIntegral.minus(_, _), IntIsIntegral.plus(_, _))

val moveKnight = (p: KnightPos) =>
	(
		for {
			a <- List(2, 1)
			b <- List(2, 1)
			fa <- fl
			fb <- fl
			if a != b
		} yield (fa(p._1, a), fb(p._2, b))
	) filter {
		case (x, y) => 1 <= x && x <= 8 && 1 <= y && y <= 8
	}

println(moveKnight (6, 2))
println(moveKnight (8, 1))

val in3 = (start: KnightPos) =>
	for {
		first <- moveKnight(start)
		second <- moveKnight(first)
		third <- moveKnight(second)
	} yield third


println(in3 (6, 2))

val canReachIn3 = (start: KnightPos, end: KnightPos) => in3(start).contains(end)

println(canReachIn3((6, 2), (6, 1)))
println(canReachIn3((6, 2), (7, 3)))
