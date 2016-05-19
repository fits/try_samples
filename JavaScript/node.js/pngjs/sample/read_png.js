'use strict';

const fs = require('fs');
const PNG = require('pngjs').PNG;

fs.createReadStream(process.argv[2])
	.pipe(new PNG())
	.on('parsed', d => console.log(d));
