
const R = require('ramda')

const data = {
	A: 1,
	B: 7,
	C: 4,
	D: 3,
	E: 10,
	F: 5,
	G: 2
}

const sortByValue = R.pipe(
	R.toPairs,
	R.sortWith([R.descend(R.prop(1))]),
	R.fromPairs
)

console.log( sortByValue(data) )
