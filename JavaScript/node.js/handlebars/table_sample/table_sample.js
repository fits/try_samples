
const R = require('ramda')
const Handlebars = require('handlebars')

const data = [
	{category: 'A', item: 'A01', week: '2018-01', num: 1},
	{category: 'A', item: 'A02', week: '2018-02', num: 1},
	{category: 'C', item: 'C01', week: '2018-04', num: 1},
	{category: 'C', item: 'C01', week: '2018-04', num: 10},
	{category: 'A', item: 'A01', week: '2018-01', num: 1},
	{category: 'A', item: 'A03', week: '2018-03', num: 2},
	{category: 'A', item: 'A03', week: '2018-03', num: 5},
	{category: 'B', item: 'B01', week: '2018-01', num: 1},
	{category: 'C', item: 'C02', week: '2018-03', num: 1},
	{category: 'C', item: 'C01', week: '2018-03', num: 2}
]

const liftObjIndexedN = R.curry((n, fn, data) =>
	R.reduce(
		(a, b) => R.mapObjIndexed(a), 
		fn, 
		R.range(0, n)
	)(data)
)

const groupByMulti = R.curry((fs, data) => R.reduce(
	(a, b) => liftObjIndexedN(b, R.groupBy(R.prop(fs[b])), a),
	data, 
	R.range(0, fs.length)
))

const weeks = R.pipe(
	R.pluck('week'),
	R.sort(R.comparator(R.lt)),
	R.uniq
)(data)

const tpl = `
<table>
	<tr>
		<th>category</th>
		<th>item</th>
		{{#each cols}}
		<th>{{this}}</th>
		{{/each}}
	</tr>
{{#each data as |v k|}}
	{{#each v as |v2 k2|}}
	<tr>
		{{#if @first}}
		<td rowspan="{{size v}}">{{k}}</td>
		{{/if}}
		<td>{{k2}}</td>
		{{#each ../../cols}}
		<td>{{lookup v2 this}}</td>
		{{/each}}
	</tr>
	{{/each}}
{{/each}}
</table>
`

const d1 = R.pipe(
	R.sortBy(R.prop('category')),
	groupByMulti(['category', 'item', 'week']),
	liftObjIndexedN(3, R.reduce((a, b) => a + b.num, 0))
)(data)

//console.log(d1)
//console.log(weeks)

Handlebars.registerHelper('size', obj => R.length(R.keys(obj)))

console.log(
	Handlebars.compile(tpl)({cols: weeks, data: d1})
)
