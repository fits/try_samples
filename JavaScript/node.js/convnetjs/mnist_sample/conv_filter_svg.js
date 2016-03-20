'use strict';

const Promise = require('bluebird');
const convnetjs = require('convnetjs');
const d3 = require('d3');
const jsdom = require('jsdom').jsdom;

const readFile = Promise.promisify(require('fs').readFile);

const size = 100;
const margin = 5;

const modelJsonFile = process.argv[2];

readFile(modelJsonFile).then( json => {
	const net = new convnetjs.Net();
	net.fromJSON(JSON.parse(json));

	return net.layers;
}).then( layers =>
	layers.reduce( (acc, v) => {
		if (v.filters) {
			acc.push( v.filters );
		}

		return acc;
	}, [])
).then( filtersList => {
	const document = jsdom();

	const svg = d3.select(document.body)
					.append('svg')
					.attr('xmlns', 'http://www.w3.org/2000/svg');

	filtersList.forEach( (fs, j) => {
		const yPos = (size + margin) * j;

		fs.forEach( (f, i) => {
			const xPos = (size + margin) * i;

			const g = svg.append('g')
						.attr('transform', `translate(${xPos}, ${yPos})`);

			const xScale = d3.scale.linear()
							.range([0, size]).domain([0, f.sx]);

			const yScale = d3.scale.linear()
							.range([0, size]).domain([0, f.sy]);

			const maxW = Math.max.apply(null, f.w);
			const minW = Math.min.apply(null, f.w);

			const pixel = d3.scale.linear()
							.range([0, 255]).domain([minW, maxW]);

			for (let y = 0; y < f.sy; y++) {
				for (let x = 0; x < f.sx; x++) {

					const p = pixel( f.get(x, y, 0) );

					g.append('rect')
						.attr('x', xScale(x))
						.attr('y', yScale(y))
						.attr('width', xScale(1))
						.attr('height', yScale(1))
						.attr('fill', d3.rgb(p, p, p));
				}
			}
		});
	});

	return document.body.innerHTML;

}).then( svg =>
	console.log(svg)
).catch( e => 
	console.error(e)
);
