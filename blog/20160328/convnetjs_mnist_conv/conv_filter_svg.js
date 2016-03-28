'use strict';

const Promise = require('bluebird');
const convnetjs = require('convnetjs');
const d3 = require('d3');
const jsdom = require('jsdom').jsdom;

const readFile = Promise.promisify(require('fs').readFile);

const size = 50;
const margin = 5;

const modelJsonFile = process.argv[2];

const valueRange = fs => fs.reduce( (acc, f) =>
	[
		Math.min(acc[0], Math.min.apply(null, f.w)),
		Math.max(acc[1], Math.max.apply(null, f.w))
	], 
	[0, 0]
);

readFile(modelJsonFile).then( json => {
	const net = new convnetjs.Net();
	net.fromJSON(JSON.parse(json));

	return net.layers;
}).then( layers =>
	layers.reduce( (acc, v) => {
		if (v.layer_type == 'conv' && v.filters) {
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

		const pixelScale = d3.scale.linear()
							.range([0, 255]).domain(valueRange(fs));

		fs.forEach( (f, i) => {
			const xPos = (size + margin) * i;

			const g = svg.append('g')
						.attr('transform', `translate(${xPos}, ${yPos})`);

			const xScale = d3.scale.linear()
							.range([0, size]).domain([0, f.sx]);

			const yScale = d3.scale.linear()
							.range([0, size]).domain([0, f.sy]);

			for (let y = 0; y < f.sy; y++) {
				for (let x = 0; x < f.sx; x++) {
					const p = pixelScale( f.get(x, y, 0) );

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
