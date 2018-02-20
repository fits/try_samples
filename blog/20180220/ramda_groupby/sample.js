
const R = require('ramda')

const data = [
	{category: 'A', item: 'A01', date: '2018-02-01', value: 1},
	{category: 'A', item: 'A02', date: '2018-02-01', value: 1},
	{category: 'A', item: 'A01', date: '2018-02-01', value: 1},
	{category: 'A', item: 'A01', date: '2018-02-02', value: 20},
	{category: 'A', item: 'A03', date: '2018-02-03', value: 2},
	{category: 'B', item: 'B01', date: '2018-02-02', value: 1},
	{category: 'A', item: 'A03', date: '2018-02-03', value: 5},
	{category: 'A', item: 'A01', date: '2018-02-02', value: 2},
	{category: 'B', item: 'B01', date: '2018-02-03', value: 3},
	{category: 'B', item: 'B01', date: '2018-02-04', value: 1},
	{category: 'C', item: 'C01', date: '2018-02-01', value: 1},
	{category: 'B', item: 'B01', date: '2018-02-04', value: 10}
]

const applyObjIndexedN = R.curry((n, fn, data) =>
	R.reduce(
		(a, b) => R.mapObjIndexed(a), 
		fn, 
		R.range(0, n)
	)(data)
)

const groupByMulti = R.curry((fields, data) => 
	R.reduce(
		(a, b) => applyObjIndexedN(b, R.groupBy(R.prop(fields[b])), a),
		data, 
		R.range(0, fields.length)
	)
)


const cols = ['category', 'item', 'date']

const sumValue = R.reduce((a, b) => a + b.value, 0)

const sumMultiGroups = R.pipe(
	groupByMulti(cols),
	applyObjIndexedN(cols.length, sumValue)
)

console.log( sumMultiGroups(data) )
