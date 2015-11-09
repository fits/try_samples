
def divideList2 = { xs, n ->
	int q = xs.size() / n
	int m = xs.size() % n

	n = Math.min(n, xs.size())

	(0..<n).inject([]) { acc, i ->
		def fr = acc*.size().sum(0)
		def to = fr + q + ((i < m)? 1: 0)

		acc << xs[fr..<to]
	}
}

println divideList2(0..<8, 3)
println divideList2(0..<7, 3)
println divideList2(0..<6, 3)

println divideList2(0..<6, 6)

println divideList2(0..<6, 10)

