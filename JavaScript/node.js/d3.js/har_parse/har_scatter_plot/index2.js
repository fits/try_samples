
const d3 = require('d3');

const jsdom = require('jsdom');
const { JSDOM } = jsdom;

const w = 800;
const h = 500;
const margin = { left: 80, top: 20, bottom: 50, right: 200 };
const legendMargin = { top: 30 };

const fontSize = '12pt';
const circleRadius = 5;

let xCol = 'time';
let yCol = 'bodySize';

const colorMap = {
	'javascript': 'blue',
	'x-javascript': 'blue',
	'json': 'green',
	'gif': 'tomato',
	'jpeg': 'red',
	'png': 'pink',
	'html': 'lime',
	'css': 'turquoise'
};

const dom = new JSDOM();
const document = dom.window.document;

const toSubType = mtype => {
	if (mtype) {
		return mtype.split(';')[0].split('/').pop();
	}
	return 'null';
};
const toColor = stype => colorMap[stype] ? colorMap[stype] : 'black';

process.stdin.resume();

let json = '';

process.stdin.on('data', chunk => json += chunk);

process.stdin.on('end', () => {
	const data = JSON.parse(json);

	const df = data.log.entries.map( d => {
		return {
			'url': d.request.url,
			'subType': toSubType(d.response.content.mimeType),
			'bodySize': d.response.bodySize,
			'time': d.time,
			'blocked': d.timings.blocked,
			'wait': d.timings.wait,
			'receive': d.timings.receive
		};
	});

	if (process.argv.length > 2) {
		xCol = process.argv[2];
	}

	if (process.argv.length > 3) {
		yCol = process.argv[3];
	}

	const initVar = {};
	initVar[xCol] = 0;
	initVar[yCol] = 0;

	const range = df.reduce(
		(acc, v) => {
			const res = {};
			res[xCol] = Math.max(acc[xCol], v[xCol]);
			res[yCol] = Math.max(acc[yCol], v[yCol]);
			return res;
		},
		initVar
	);

	if (process.argv.length > 4) {
		range[xCol] = parseInt(process.argv[4]);
	}

	if (process.argv.length > 5) {
		range[yCol] = parseInt(process.argv[5]);
	}

	const xDomain = [0, range[xCol]];
	const yDomain = [0, range[yCol]];

	const svg = d3.select(document.body)
		.append('svg')
			.attr('xmlns', 'http://www.w3.org/2000/svg')
			.attr('width', w + margin.left + margin.right)
			.attr('height', h + margin.top + margin.bottom)
			.append('g')
				.attr('transform', `translate(${margin.left}, ${margin.top})`);

	const x = d3.scaleLinear().range([0, w]).domain(xDomain);
	const y = d3.scaleLinear().range([h, 0]).domain(yDomain);

	const xAxis = d3.axisBottom(x);
	const yAxis = d3.axisLeft(y);

	svg.append('g')
		.attr('transform', `translate(0, ${h})`)
		.call(xAxis);

	svg.append('g')
		.call(yAxis);

	svg.append('text')
		.attr('x', w / 2)
		.attr('y', h + 40)
		.style('font-size', fontSize)
		.text(xCol);

	svg.append('text')
		.attr('x', -h / 2)
		.attr('y', -(margin.left) / 1.5)
		.style('font-size', fontSize)
		.attr('transform', 'rotate(-90)')
		.text(yCol);

	const point = svg.selectAll('circle')
		.data(df)
		.enter()
			.append('circle');

	point.attr('class', d => d.subType)
		.attr('cx', d => x(d[xCol]))
		.attr('cy', d => y(d[yCol]))
		.attr('r', circleRadius)
		.attr('fill', d => toColor(d.subType))
		.append('title')
			.text(d => d.url);

	const legend = svg.selectAll('.legend')
		.data(d3.entries(colorMap))
		.enter()
			.append('g')
				.attr('class', 'legend')
				.attr('transform', (d, i) => {
					const left = w + margin.left;
					const top = margin.top + i * legendMargin.top;

					return `translate(${left}, ${top})`;
				});

	legend.append('circle')
		.attr('r', circleRadius)
		.attr('fill', d => d.value);

	legend.append('text')
		.attr('x', circleRadius * 2)
		.attr('y', 4)
		.style('font-size', fontSize)
		.attr('onmouseover', d => 
			`document.querySelectorAll('circle.${d.key}').forEach(d => d.setAttribute('r', ${circleRadius} * 2))`)
		.attr('onmouseout', d => 
			`document.querySelectorAll('circle.${d.key}').forEach(d => d.setAttribute('r', ${circleRadius}))`)
		.text(d => d.key);

	console.log(document.body.innerHTML);
});
