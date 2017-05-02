
const d3 = require('d3');

const jsdom = require('jsdom');
const { JSDOM } = jsdom;

const w = 800;
const h = 500;
const margin = { left: 50, top: 20, bottom: 50, right: 30 };

const xDomain = [0, 5000];
const yDomain = [0, 50000];

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

const toSubType = mtype => mtype.split(';')[0].split('/')[1];
const toColor = stype => colorMap[stype] ? colorMap[stype] : 'black';

process.stdin.resume();

let json = '';

process.stdin.on('data', chunk => json += chunk);

process.stdin.on('end', () => {
	const data = JSON.parse(json);

	const df = data.log.entries.map( d => {
		return {
			'subType': toSubType(d.response.content.mimeType),
			'bodySize': d.response.bodySize,
			'time': d.time
		};
	});

	const svg = d3.select(document.body).append('svg')
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
		.text('Time (ms)');

	const cr = svg.selectAll('circle')
					.data(df)
					.enter()
						.append('circle');

	cr.attr('cx', d => x(d.time))
		.attr('cy', d => y(d.bodySize))
		.attr('r', 5)
		.attr('fill', d => toColor(d.subType));

	console.log(document.body.innerHTML);
});


