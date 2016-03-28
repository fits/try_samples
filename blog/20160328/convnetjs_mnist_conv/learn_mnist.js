'use strict';

const Promise = require('bluebird');
const fs = require('fs');
const shuffle = require('shuffle-array');
const convnetjs = require('convnetjs');
const readFile = Promise.promisify(fs.readFile);
const writeFile = Promise.promisify(fs.writeFile);

const mnist = require('./load_mnist');

const epoch = parseInt(process.argv[2]);
const batchSize = parseInt(process.argv[3]);
const learningRate = parseFloat(process.argv[4]);
const trainMethod = process.argv[5];

const modelJsonFile = process.argv[6];
const modelJsonDestFile = process.argv[7];

const range = n => {
	const res = Array(n);

	for (let i = 0; i < n; i++) {
		res[i] = i;
	}

	return res;
};

const createLogger = (logSize, logFunc) => {
	let list = [];
	let counter = 0;

	return (loss, accuracy) => {
		list.push({loss: loss, accuracy: accuracy});

		const size = list.length;

		if (size >= logSize) {
			const res = list.reduce(
				(acc, d) => {
					acc.loss += d.loss;
					acc.accuracy += d.accuracy;

					return acc;
				},
				{ loss: 0.0, accuracy: 0 }
			);

			logFunc(
				res.loss / size,
				res.accuracy / size,
				counter++
			);

			list = [];
		}
	};
};


Promise.all([
	readFile(modelJsonFile),
	mnist.loadMnist('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')
]).spread( (json, data) => {
	const net = new convnetjs.Net();
	net.fromJSON(JSON.parse(json));

	const trainer = new convnetjs.Trainer(net, {
		method: trainMethod, 
		batch_size: batchSize, 
		learning_rate: learningRate
	});

	range(epoch).forEach(ep => {
		const log = createLogger(batchSize, (loss, acc, counter) =>
			console.log( [ep, counter, loss, acc].join(',') )
		);

		shuffle(range(data.length)).forEach(i => {
			const d = data[i];
			const stats = trainer.train(d.values, d.label);

			log(
				stats.loss,
				(net.getPrediction() == d.label)? 1: 0
			);
		});
	});

	return net;

}).then( net => 
	writeFile(modelJsonDestFile, JSON.stringify(net.toJSON()))
).catch( e => 
	console.error(e)
);
