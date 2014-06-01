import static java.util.Collections.nCopies

// (a) 1 - (AA + BB + CC)
def giniA = { xs ->
	1 - xs.countBy { it }*.value.sum { (it / xs.size()) ** 2 }
}

// (b) AB + AC + BA + BC + CA + CB
def giniB = { xs ->
	nCopies(2, xs.countBy { it }).combinations().findAll {
		it.first().key != it.last().key
	}.sum {
		(it.first().value / xs.size()) * (it.last().value / xs.size())
	}
}

def list = ['A', 'B', 'B', 'C', 'B', 'A']

println giniA(list)
println giniB(list)
