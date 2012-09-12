type KnightPos = Tuple2[Int, Int]

val moveKnight = (p: KnightPos) => List(
	(p._1 + 2, p._2 - 1), (p._1 + 2, p._2 + 1),
	(p._1 - 2, p._2 - 1), (p._1 - 2, p._2 + 1),
	(p._1 + 1, p._2 - 2), (p._1 + 1, p._2 + 2),
	(p._1 - 1, p._2 - 2), (p._1 - 1, p._2 + 2)
).filter { case (x, y) => 1 <= x && x <= 8 && 1 <= y && y <= 8 }

val in3 = (start: KnightPos) =>
	for {
		first <- moveKnight(start)
		second <- moveKnight(first)
		third <- moveKnight(second)
	} yield third


val canReachIn3 = (start: KnightPos, end: KnightPos) => in3(start).contains(end)

println(moveKnight (8, 1))

println(in3 (6, 2))

println(canReachIn3((6, 2), (6, 1)))
println(canReachIn3((6, 2), (7, 3)))
