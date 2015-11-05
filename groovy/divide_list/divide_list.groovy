
def divideAlloc = { x, n ->
	int q = x / n
	int m = x % n

	(0..<n).collect { q + ((it < m)? 1: 0) }
}

def divideList = { xs, n ->
	int q = xs.size() / n
	int m = xs.size() % n

	(0..<n).inject([]) { acc, i ->
		def s = acc*.size().sum(0)
		def e = s + q + ((i < m)? 1: 0)

		acc << xs[s..<e]
	}
}

def divideList2 = { xs, n ->
	int q = xs.size() / n
	int m = xs.size() % n

	(0..<n).inject([0, []]) { acc, i ->
		int p = acc.first() + q + ((i < m)? 1: 0)

		[p, acc.last() << xs[acc.first()..<p]]
	}.last()
}


println divideAlloc(11, 3)

println divideList(0..<11, 3)
println divideList(0..<11, 4)

println divideList(0..<8, 3)
println divideList(0..<7, 3)
println divideList(0..<6, 3)
println divideList(0..<8, 5)

println '------'

println divideList2(0..<8, 3)
println divideList2(0..<7, 3)
println divideList2(0..<6, 3)

