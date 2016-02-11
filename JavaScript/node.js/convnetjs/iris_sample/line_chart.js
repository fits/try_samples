
var Promise = require('bluebird');
var basicCsv = require('basic-csv');
var d3 = require('d3');
var jsdom = require('jsdom').jsdom;

var readCSV = Promise.promisify(basicCsv.readCSV);

var document = jsdom();

// レイアウトの生成
var chartLayout = (xnum, w, h, margin) => {
	var borderWidth = w + margin.left + margin.right;
	var borderHeight = h + margin.top + margin.bottom;

	var svg = d3.select(document.body).append('svg')
		.attr('xmlns', 'http://www.w3.org/2000/svg')
		.attr('width', xnum * borderWidth)
		.attr('height', borderHeight);

	return Array(xnum).fill(0).map( (n, i) =>
		svg.append('g')
			.attr('transform', `translate(${i * borderWidth + margin.left}, ${margin.top})`)
	);
};

var w = 300;
var h = 300;
var margin = { top: 20, bottom: 50, left: 50, right: 20 };
var xDomain = [0, 50];
var yDomain = [1, 0];

var xLabels = ['回数', '回数'];
var yLabels = ['誤差', '正解率'];

// スケールの定義
var x = d3.scale.linear().range([0, w]).domain(xDomain);
var y = d3.scale.linear().range([0, h]).domain(yDomain);

// 軸の定義
var xAxis = d3.svg.axis().scale(x).orient('bottom');
var yAxis = d3.svg.axis().scale(y).orient('left');

// 折れ線の作成
var createLine = d3.svg.line()
	.x((d, i) => x(i + 1))
	.y(d => y(d));

var gs = chartLayout(2, w, h, margin);

// X・Y軸の描画
gs.forEach( (g, i) => {
	g.append('g')
		.attr('transform', `translate(0, ${h})`)
		.call(xAxis)
		.append('text')
			.attr('x', w / 2)
			.attr('y', 30)
			.text(xLabels[i]);

	g.append('g')
		.call(yAxis)
		.append('text')
			.attr('x', -h / 2)
			.attr('y', -30)
			.attr('transform', 'rotate(-90)')
			.text(yLabels[i]);
});

readCSV(process.argv[2])
	.then( ds => {

		gs[0].append('path')
			.attr('d', createLine(ds.map(d => d[0])))
			.attr('stroke', 'blue')
			.attr('fill', 'none');

		gs[0].append('path')
			.attr('d', createLine(ds.map(d => d[2])))
			.attr('stroke', 'red')
			.attr('fill', 'none');

		gs[1].append('path')
			.attr('d', createLine(ds.map(d => d[1])))
			.attr('stroke', 'blue')
			.attr('fill', 'none');

		gs[1].append('path')
			.attr('d', createLine(ds.map(d => d[3])))
			.attr('stroke', 'red')
			.attr('fill', 'none');

		console.log(document.body.innerHTML);
	});
