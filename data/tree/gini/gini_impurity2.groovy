
import static java.util.Collections.*

def list = ['A', 'B', 'B', 'C', 'A', 'B']

println 1 - list.countBy { it }*.value.sum { (it / list.size()) ** 2 }
println 1 - list.countBy { it }.values().sum { (it / list.size()) ** 2 }

println nCopies(2, list.countBy { it }).combinations().findAll {
	it[0].key != it[1].key
}.sum {
	(it[0].value / list.size()) * (it[1].value / list.size())
}

println nCopies(2, list.countBy { it }).combinations().findAll {
	it.first().key != it.last().key
}.sum {
	(it.first().value / list.size()) * (it.last().value / list.size())
}

println nCopies(2, list.countBy { it }).combinations().findAll {
	it.first().key != it.last().key
}.sum { it.inject(1) { acc, x -> x.value / list.size() * acc } }


println nCopies(2, list.countBy { it }).combinations().findAll {
	it[0].key != it[1].key
}*.value.sum { (it[0] / list.size()) * (it[1] / list.size()) }

println nCopies(2, list.countBy { it }).combinations { x, y ->
	[ [x.key, y.key], (x.value / list.size()) * (y.value / list.size()) ]
}.findAll {
	it[0][0] != it[0][1]
}.sum { it[1] }

