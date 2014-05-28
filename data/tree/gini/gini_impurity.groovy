// Gini Impurity

def calcGini = { list ->
	def counts = list.countBy { it }

	1 - counts.inject(0) { acc, k, v -> acc + (v / list.size()) ** 2 }
}

def calcGini2 = { list ->
	def counts = list.countBy { it }

	def prob = { counts[it] / list.size() }

	Collections.nCopies(2, counts.keySet()).combinations().findAll {
		it[0] != it[1]
	}.inject(0) { acc, val ->
		prob(val[0]) * prob(val[1]) + acc
	}
}

def list = ['A', 'B', 'B', 'C', 'B', 'A']

println calcGini(list)
println calcGini2(list)
