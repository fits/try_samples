
var basicCsv = require('basic-csv');
var shuffle = require('shuffle-array');
var convnetjs = require('convnetjs');

var epoch = 1000;
var trainRate = 0.7;
var learningRate = 0.01;
var batchSize = 1;
var trainMethod = 'adam';

var categoryMap = {
	'Iris-setosa': 0,
	'Iris-versicolor': 1,
	'Iris-virginica': 2
};

var layer_defs = [
	{ type: 'input', out_sx: 1, out_sy: 1, out_depth: 4 },
	{ type: 'softmax', num_classes: 3 }
]

var net = new convnetjs.Net();
net.makeLayers(layer_defs);

var trainer = new convnetjs.Trainer(net, {
	method: trainMethod,
	batch_size: batchSize,
	learning_rate: learningRate
});

var createData = r => new convnetjs.Vol([ r[0], r[1], r[2], r[3] ]);

basicCsv.readCSV('iris.data', (err, data) => {

	var trainSize = Math.floor(data.length * trainRate);

	for (var i = 0; i < epoch; i++) {

		var trainData = shuffle(data, {copy: true});
		var testData = trainData.splice(trainSize);

		var trainLossSum = trainData.reduce( (acc, r) => {
			var stats = trainer.train(createData(r), categoryMap[r[4]]);
			return acc + stats.loss;
		}, 0.0)

		var testRes = testData.reduce( (acc, r) => {
			net.forward(createData(r));

			var actual = net.getPrediction();
			var expect = categoryMap[r[4]];

			var loss = net.backward(expect);

			return {
				loss: acc.loss + loss,
				accuracy: acc.accuracy + (actual == expect? 1: 0)
			};
		}, {loss: 0.0, accuracy: 0});

		var trainLoss = trainLossSum / trainData.length;
		var testLoss = testRes.loss / testData.length;
		var testAccuracy = testRes.accuracy / testData.length;

		console.log( [trainLoss, testLoss, testAccuracy].join(',') );
	}
});
