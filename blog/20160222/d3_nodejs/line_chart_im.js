
var d3 = require('d3');
var jsdom = require('jsdom').jsdom;

var w = 400;
var h = 300;
var margin = { left: 50, top: 20, bottom: 50, right: 20 };
var xDomain = [0, 10];
var yDomain = [15, 0];

var document = jsdom();

var data = [3, 5, 4, 6, 9, 2, 8, 7, 5, 11];

var svg = d3.select(document.body).append('svg')
	.attr('xmlns', 'http://www.w3.org/2000/svg')
	.attr('width', w + margin.left + margin.right)
	.attr('height', h + margin.top + margin.bottom)
	.append('g')
		.attr('transform', `translate(${margin.left}, ${margin.top})`);

// スケールの定義
var x = d3.scale.linear().range([0, w]).domain(xDomain);
var y = d3.scale.linear().range([0, h]).domain(yDomain);

// 軸の定義
var xAxis = d3.svg.axis().scale(x).orient('bottom');
var yAxis = d3.svg.axis().scale(y).orient('left');

// 横軸の描画
svg.append('g')
	.attr('transform', `translate(0, ${h})`)
	.call(xAxis)
	.append('text')
		.attr('x', w / 2)
		.attr('y', 35)
		.style('font-family', 'Sans')
		.text('番号');

// 縦軸の描画
svg.append('g')
	.call(yAxis)
	.append('text')
		.attr('x', -h / 2)
		.attr('y', -30)
		.attr('transform', 'rotate(-90)')
		.style('font-family', 'Sans')
		.text('個数');

// 折れ線の描画関数
var createLine = d3.svg.line()
	.x( (d, i) => x(i + 1) )
	.y( d => y(d) );

// 折れ線の描画
svg.append('path')
	.attr('d', createLine(data))
	.attr('stroke', 'blue')
	.attr('fill', 'none');

console.log(document.body.innerHTML);
