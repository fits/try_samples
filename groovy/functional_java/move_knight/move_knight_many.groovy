@Grab("org.functionaljava:functionaljava:3.1")
import groovy.transform.Immutable
import fj.F
import fj.F2
import static fj.data.List.*

@Immutable class KnightPos {
	int x
	int y
}

def pos = {x, y -> new KnightPos(x: x, y: y)}

def moveKnight = {KnightPos p ->
	list(
		pos(p.x + 2, p.y - 1), pos(p.x + 2, p.y + 1),
		pos(p.x - 2, p.y - 1), pos(p.x - 2, p.y + 1),
		pos(p.x + 1, p.y - 2), pos(p.x + 1, p.y + 2),
		pos(p.x - 1, p.y - 2), pos(p.x - 1, p.y + 2)
	).filter({ 1<= it.x && it.x <= 8 && 1 <= it.y && it.y <= 8 } as F)
} as F

def inMany = {int x, KnightPos p ->
	list(p).bind replicate(x, moveKnight).foldLeft1({b, a ->
		{ n -> b.f(n).bind(a) } as F
	} as F2)
}

def canReachIn = {int x, KnightPos start, KnightPos end ->
	inMany(x, start).exists({ it == end } as F)
}

println inMany(3, pos(6, 2))

println canReachIn(3, pos(6, 2), pos(6, 1))
println canReachIn(3, pos(6, 2), pos(7, 3))

