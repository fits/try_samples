
const elasticsearch = require('elasticsearch');
const R = require('ramda');

const client = new elasticsearch.Client({
	host: 'localhost:9200',
	log: 'trace'
});

const groupBy = R.groupBy(R.prop('category'));

const sumValue = R.pipe(
	R.map(R.prop('value')),
	R.sum
);

client.search({
	index: 'sample',
	type: 'data',
	sort: 'category.keyword'
})
.then( res => res.hits.hits )
.then( hits => R.map(d => d._source, hits) )
.then( groupBy )
.then( R.map(sumValue) )
.then( console.log );
