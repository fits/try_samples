
var Promise = require('bluebird');
var fs = require('fs');
var convnetjs = require('convnetjs');

var mnist = require('./parse_mnist');

var jsonFile = process.argv[2];

var readFile = Promise.promisify(fs.readFile);

Promise.all([
	readFile(jsonFile),
	mnist.parse('t10k-images.idx3-ubyte', 't10k-labels.idx1-ubyte')
]).spread( (json, data) => {

	var net = new convnetjs.Net();

	net.fromJSON(JSON.parse(json));

	var count = data.reduce((acc, d) => {
		net.forward(d.values);

		return acc + (d.label == net.getPrediction()? 1: 0);
	}, 0);

	console.log(`result: ${count / data.length}`);
});
