"use strict";

const Promise = require('bluebird');
const fs = require('fs');
const shuffle = require('shuffle-array');
const convnetjs = require('convnetjs');

const mnist = require('./parse_mnist');

const readFile = Promise.promisify(fs.readFile);
const writeFile = Promise.promisify(fs.writeFile);

const epoch = 5;
const batchSize = 20;
const learningRate = 0.001;
const trainMethod = 'adadelta';

const jsonFile = process.argv[2];
const jsonDestFile = process.argv[3];

const range = n => {
	const res = Array(n);

	for (let i = 0; i < n; i++) {
		res[i] = i;
	}

	return res;
};

const shuffleRange = n => shuffle(range(n));

Promise.all([
	readFile(jsonFile),
	mnist.parse('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')
]).spread( (json, data) => {

	const net = new convnetjs.Net();

	net.fromJSON(JSON.parse(json));

	const trainer = new convnetjs.Trainer(net, {
		method: trainMethod, 
		batch_size: batchSize, 
		learning_rate: learningRate
	});

	for (let i = 0; i < epoch; i++) {

		shuffleRange(data.length).forEach(i => {
			const d = data[i];
			const stats = trainer.train(d.values.clone(), d.label);

			console.log(stats.loss);
		});
	}

	writeFile(jsonDestFile, JSON.stringify(net.toJSON()))
		.error( e => console.error(e) );
});
