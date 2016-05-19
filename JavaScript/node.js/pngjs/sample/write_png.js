'use strict';

const fs = require('fs');
const PNG = require('pngjs').PNG;

const img = new PNG({
	width: 100,
	height: 80,
	fill: true
});

for (let i = 0; i < 1000; i++) {
	let p = i * 4;

	img.data[p] = 200;     // R
	img.data[p + 1] = 100; // G
	img.data[p + 2] = 220; // B
	img.data[p + 3] = 255; // A
}

img.pack().pipe(fs.createWriteStream('sample.png'));
