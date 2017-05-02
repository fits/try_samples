
const d3 = require('d3');

const jsdom = require('jsdom');
const { JSDOM } = jsdom;

const w = 800;
const h = 500;
const margin = { left: 80, top: 20, bottom: 50, right: 150 };
const legendMargin = { top: 30 };

const fontSize = '12pt';
const circleRadius = 5;

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
		.text('Time (ms)');

	svg.append('text')
		.attr('x', -h / 2)
		.attr('y', -(margin.left) / 1.5)
		.style('font-size', fontSize)
		.attr('transform', 'rotate(-90)')
		.text('Body Size');

	const point = svg.selectAll('circle')
		.data(df)
		.enter()
			.append('circle');

	point.attr('class', d => d.subType)
		.attr('cx', d => x(d.time))
		.attr('cy', d => y(d.bodySize))
		.attr('r', circleRadius)
		.attr('fill', d => toColor(d.subType));

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
