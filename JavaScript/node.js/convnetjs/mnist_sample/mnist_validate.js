"use strict";

const Promise = require('bluebird');
const convnetjs = require('convnetjs');
const readFile = Promise.promisify(require('fs').readFile);

const mnist = require('./parse_mnist');

const modelJsonFile = process.argv[2];

Promise.all([
	readFile(modelJsonFile),
	mnist.parse('t10k-images.idx3-ubyte', 't10k-labels.idx1-ubyte')
]).spread( (json, data) => {

	const net = new convnetjs.Net();
	net.fromJSON(JSON.parse(json));

	const accuCount = data.reduce( (acc, d) => {
		net.forward(d.values);

		return acc + (d.label == net.getPrediction()? 1: 0);
	}, 0);

	console.log(`data size: ${data.length}`);
	console.log(`accuracy: ${accuCount / data.length}`);
});
