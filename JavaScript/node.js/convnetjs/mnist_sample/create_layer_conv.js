
var Promise = require('bluebird');
var fs = require('fs');
var convnetjs = require('convnetjs');

var jsonDestFile = process.argv[2];

var writeFile = Promise.promisify(fs.writeFile);

var layer_defs = [
	{ type: 'input', out_sx: 28, out_sy: 28, out_depth: 1 },
	{ type: 'conv', sx: 5, filters: 8, stride: 1, pad: 2, activation: 'relu' },
	{ type: 'pool', sx: 2, stride: 2 },
	{ type: 'conv', sx: 5, filters: 16, stride: 1, pad: 2, activation: 'relu' },
	{ type: 'pool', sx: 3, stride: 3 },
	{ type: 'softmax', num_classes: 10 }
];

var net = new convnetjs.Net();
net.makeLayers(layer_defs);

writeFile(jsonDestFile, JSON.stringify(net.toJSON()))
	.error( e => console.error(e) );
