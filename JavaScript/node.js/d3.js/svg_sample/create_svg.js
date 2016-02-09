
var d3 = require('d3');
var jsdom = require('jsdom').jsdom;

var w = 200;
var h = 100;

var document = jsdom();

var svg = d3.select(document.body).append('svg')
	.attr('xmlns', 'http://www.w3.org/2000/svg')
	.attr('width', w)
	.attr('height', h);

var rc = svg.selectAll('rect')
	.data([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
	.enter().append('rect');

rc.attr('x', (d, i) => i * 10)
	.attr('y', 0)
	.attr('width', 10)
	.attr('height', 100)
	.attr('fill', (d, i) => `rgb(${d * 25}, ${d * 25}, ${d * 25})`);

console.log(document.body.innerHTML);
