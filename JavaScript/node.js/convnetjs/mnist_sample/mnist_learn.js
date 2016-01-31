
var Promise = require('bluebird');
var fs = require('fs');
var shuffle = require('shuffle-array');
var convnetjs = require('convnetjs');

var mnist = require('./parse_mnist');

var epoch = 5;
var batchSize = 20;
var learningRate = 0.001;
var trainMethod = 'adam';

var jsonFile = process.argv[2];
var jsonDestFile = process.argv[3];

var readFile = Promise.promisify(fs.readFile);
var writeFile = Promise.promisify(fs.writeFile);

var range = n => {
	var res = Array(n);

	for (var i = 0; i < n; i++) {
		res[i] = i;
	}

	return res;
};

var shuffleRange = n => shuffle(range(n));

Promise.all([
	readFile(jsonFile),
	mnist.parse('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')
]).spread( (json, data) => {

	var net = new convnetjs.Net();

	net.fromJSON(JSON.parse(json));

	var trainer = new convnetjs.Trainer(net, {
		method: trainMethod, 
		batch_size: batchSize, 
		learning_rate: learningRate
	});

	for (var i = 0; i < epoch; i++) {

		shuffleRange(data.length).forEach(i => {
			var d = data[i];
			var stats = trainer.train(d.values.clone(), d.label);

			//	console.log(stats.loss);
		});
	}

	writeFile(jsonDestFile, JSON.stringify(net.toJSON()))
		.error( e => console.error(e) );
});
