
const R = require('ramda')

const data = [
	{item: 'A01', week: '2018-01', num: 1},
	{item: 'A01', week: '2018-04', num: 1},
	{item: 'A01', week: '2018-01', num: 1},
	{item: 'A01', week: '2018-03', num: 2},
	{item: 'A01', week: '2018-03', num: 5},
	{item: 'B02', week: '2018-03', num: 1},
	{item: 'B02', week: '2018-04', num: 3},
	{item: 'B02', week: '2018-03', num: 4}
]

const stocks = [
	{item: 'A01', week: '2018-04', num: 2},
	{item: 'B02', week: '2018-04', num: 5},
	{item: 'C03', week: '2018-04', num: 1}
]

const liftObjIndexedN = R.curry((n, fn, d) =>
	R.reduce(
		(a, b) => R.mapObjIndexed(a), 
		fn, 
		R.range(0, n)
	)(d)
)

const groupByMulti = R.curry((fs, d) => R.reduce(
	(a, b) => liftObjIndexedN(b, R.groupBy(R.prop(fs[b])), a),
	d, 
	R.range(0, fs.length)
))

const groupSum =R.pipe(
	groupByMulti(['item', 'week']),
	liftObjIndexedN(2, R.reduce((a, b) => a + b.num, 0))
)

const tr = groupSum(data)
const st = groupSum(stocks)

console.log(tr)
console.log(st)

console.log('-----')

const fst = R.mapObjIndexed(
	(v, k) => {
		const s = R.pipe(R.toPairs, R.head)(v)

		return R.reduce(
			(a, b) => a + ((b[0] <= s[0]) ? b[1] : 0),
			s[1],
			R.ifElse(
				R.has(k),
				R.pipe(R.prop(k), R.toPairs),
				R.always([])
			)(tr)
		)
	},
	st
)

console.log(fst)

const weeks = ['2018-01', '2018-02', '2018-03', '2018-04']

const ast = R.mapObjIndexed(
	(v, k) => R.last(
		R.reduce(
			(a, b) => {
				const n = R.pipe(
					R.ifElse(
						R.has(k),
						R.pipe(R.prop(k)),
						R.always({})
					),
					R.ifElse(
						R.has(b),
						R.prop(b),
						R.always(0)
					)
				)(tr)

				return [a[0] - n, R.assoc(b, a[0] - n, a[1])]
			},
			[v, {}],
			weeks
		)
	),
	fst
)

console.log(ast)
