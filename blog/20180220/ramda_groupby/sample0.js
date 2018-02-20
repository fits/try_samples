
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

const res1 = R.groupBy(R.prop('category'), data)

console.log(res1)

const res2 = R.mapObjIndexed(R.groupBy(R.prop('item')), res1)

console.log(res2)

const res3 = R.mapObjIndexed(R.mapObjIndexed(R.groupBy(R.prop('date'))), res2)

console.log(res3)

const sumValue = R.reduce((a, b) => a + b.value, 0)

const res4 = R.mapObjIndexed(R.mapObjIndexed(R.mapObjIndexed(sumValue)), res3)

console.log(res4)
