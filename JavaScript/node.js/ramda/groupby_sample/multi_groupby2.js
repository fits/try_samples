
const R = require('ramda')

const data = [
	{category: 'A', item: 'A01', week: '2018-01', num: 1},
	{category: 'A', item: 'A02', week: '2018-02', num: 1},
	{category: 'C', item: 'C01', week: '2018-04', num: 1},
	{category: 'C', item: 'C01', week: '2018-04', num: 10},
	{category: 'A', item: 'A01', week: '2018-01', num: 1},
	{category: 'A', item: 'A03', week: '2018-03', num: 2},
	{category: 'A', item: 'A03', week: '2018-03', num: 5},
	{category: 'B', item: 'B01', week: '2018-01', num: 1}
]

const liftObjIndexedN = R.curry((n, fn) =>
	R.reduce(
		(a, b) => R.mapObjIndexed(a), 
		fn, 
		R.range(0, n)
	)
)

const groupByMulti = R.curry((fs, data) => R.reduce(
	(a, b) => liftObjIndexedN(b, R.groupBy(R.prop(fs[b])))(a),
	data, 
	R.range(0, fs.length)
))

console.log( groupByMulti(['category'], data) )
console.log( groupByMulti(['category', 'item'], data) )
console.log( groupByMulti([], data) )

const r = groupByMulti(['category', 'item', 'week'], data)

console.log(r)

console.log('-----')

const sum = liftObjIndexedN(3, R.reduce((a, b) => a + b.num, 0))

console.log( sum(r) )
