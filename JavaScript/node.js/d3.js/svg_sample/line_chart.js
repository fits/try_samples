
var d3 = require('d3');
var jsdom = require('jsdom').jsdom;

var w = 400;
var h = 300;
var margin = { left: 40, top: 20, bottom: 20, right: 20 };
var xDomain = [0, 10];
var yDomain = [15, 0];

var document = jsdom();

var data = [
	[1, 8, 4, 6, 2, 1, 8, 9, 5, 13],
	[0, 4, 5, 7, 3, 1, 3, 6, 10, 2]
];

var svg = d3.select(document.body).append('svg')
	.attr('xmlns', 'http://www.w3.org/2000/svg')
	.attr('width', w + margin.left + margin.right)
	.attr('height', h + margin.top + margin.bottom)
	.append('g').attr('transform', `translate(${margin.left}, ${margin.top})`);

// スケールの定義
var x = d3.scale.linear().range([0, w]).domain(xDomain);
var y = d3.scale.linear().range([0, h]).domain(yDomain);

// 軸の定義
var xAxis = d3.svg.axis().scale(x).orient('bottom');
var yAxis = d3.svg.axis().scale(y).orient('left');

svg.append('g')
	.attr('transform', `translate(0, ${h})`)
	.call(xAxis);

// 縦軸の描画
svg.append('g')
	.call(yAxis);

// 折れ線の描画関数
var createLine = d3.svg.line()
	.x((d, i) => x(i + 1))
	.y(d => y(d));

[
	{ values: data[0], color: 'red' },
	{ values: data[1], color: 'blue' }
].forEach(v => 
	// 折れ線の描画
	svg.append('path')
		.attr('d', createLine(v.values))
		.attr('stroke', v.color)
		.attr('fill', 'none')
);

console.log(document.body.innerHTML);
