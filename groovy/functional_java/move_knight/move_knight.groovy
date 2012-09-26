@Grab("org.functionaljava:functionaljava:3.1")
import groovy.transform.Immutable
import fj.F
import static fj.data.List.*

@Immutable class KnightPos {
	int x
	int y
}

def f = { it as F }

def pos = {x, y -> new KnightPos(x: x, y: y)}

def moveKnight = {KnightPos p ->
	list(
		pos(p.x + 2, p.y - 1), pos(p.x + 2, p.y + 1),
		pos(p.x - 2, p.y - 1), pos(p.x - 2, p.y + 1),
		pos(p.x + 1, p.y - 2), pos(p.x + 1, p.y + 2),
		pos(p.x - 1, p.y - 2), pos(p.x - 1, p.y + 2)
	).filter(f { 1<= it.x && it.x <= 8 && 1 <= it.y && it.y <= 8})
} as F

def in3 = {KnightPos p ->
	list(p).bind(moveKnight).bind(moveKnight).bind(moveKnight)
}

def canReachIn3 = {KnightPos start, KnightPos end ->
	in3(start).exists(f {it == end})
}

println in3(pos(6, 2))

println canReachIn3(pos(6, 2), pos(6, 1))
println canReachIn3(pos(6, 2), pos(7, 3))

