
const R = require('ramda');

const data = [
	{id: 'a1', value: 10},
	{id: 'b2', value: 11},
	{id: 'a1', value: 5},
	{id: 'a1', value: 1},
	{id: 'b2', value: 4},
	{id: 'd4', value: 8},
	{id: 'c3', value: 7},
	{id: 'd4', value: 6}
];

console.log( R.groupBy(R.prop('id'), data) );

console.log('----------');

const groupBy = R.pipe(
	R.groupBy(R.prop('id')),
	R.values(),
	R.sortBy(R.path([0, 'id']))
);

console.log( groupBy(data) );
