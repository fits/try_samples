'use strict';

const Promise = require('bluebird');
const d3 = require('d3');
const jsdom = require('jsdom').jsdom;

const readCSV = Promise.promisify(require('basic-csv').readCSV);

const w = 300;
const h = 300;
const margin = { top: 20, bottom: 50, left: 50, right: 20 };

const xLabels = ['バッチ回数', 'バッチ回数'];
const yLabels = ['誤差', '正解率'];

readCSV(process.argv[2]).then( ds => {
	const document = jsdom();

	const chartLayout = (xnum, w, h, margin) => {
		const borderWidth = w + margin.left + margin.right;
		const borderHeight = h + margin.top + margin.bottom;

		const svg = d3.select(document.body).append('svg')
			.attr('xmlns', 'http://www.w3.org/2000/svg')
			.attr('width', xnum * borderWidth)
			.attr('height', borderHeight);

		return Array(xnum).fill(0).map( (n, i) =>
			svg.append('g')
				.attr('transform', `translate(${i * borderWidth + margin.left}, ${margin.top})`)
		);
	};

	const xDomain = [0, ds.length];
	const yDomain = [1, 0];

	// スケールの定義
	const x = d3.scale.linear().range([0, w]).domain(xDomain);
	const y = d3.scale.linear().range([0, h]).domain(yDomain);

	// 軸の定義
	const xAxis = d3.svg.axis().scale(x).orient('bottom').ticks(5);
	const yAxis = d3.svg.axis().scale(y).orient('left');

	// 折れ線の作成
	const createLine = d3.svg.line()
		.x((d, i) => x(i + 1))
		.y(d => {
			if (d == 'Infinity') {
				d = 1000;
			}
			return y(d);
		});

	// 折れ線の描画
	const drawLine = (g, data, colIndex, color) => {
		g.append('path')
			.attr('d', createLine(data.map(d => d[colIndex])))
			.attr('stroke', color)
			.attr('fill', 'none');
	};

	const gs = chartLayout(2, w, h, margin);

	// X・Y軸の描画
	gs.forEach( (g, i) => {
		g.append('g')
			.attr('transform', `translate(0, ${h})`)
			.call(xAxis)
			.append('text')
				.attr('x', w / 2)
				.attr('y', 35)
				.style('font-family', 'Sans')
				.text(xLabels[i]);

		g.append('g')
			.call(yAxis)
			.append('text')
				.attr('x', -h / 2)
				.attr('y', -35)
				.attr('transform', 'rotate(-90)')
				.style('font-family', 'Sans')
				.text(yLabels[i]);
	});

	drawLine(gs[0], ds, 2, 'blue');
	drawLine(gs[1], ds, 3, 'blue');

	return document.body.innerHTML;

}).then( html => 
	console.log(html)
);
