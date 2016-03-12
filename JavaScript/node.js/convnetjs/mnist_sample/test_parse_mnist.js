"use strict";

const mnist = require('./parse_mnist');

const printData = d => {
	console.log(`***** number = ${d.label} *****`);

	const v = d.values;

	for (let y = 0; y < v.sy; y++) {
		const r = Array(v.sx);

		for (let x = 0; x < v.sx; x++) {
			r[x] = v.get(x, y, 0) > 0 ? '#' : ' ';
		}

		console.log(r.join(''));
	}
};

mnist.parse('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')
	.then(ds => {
		console.log(`size: ${ds.length}`);

		printData(ds[0]);
		printData(ds[1]);
	});
