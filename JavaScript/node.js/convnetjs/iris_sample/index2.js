
var Promise = require('bluebird');
var basicCsv = require('basic-csv');
var shuffle = require('shuffle-array');
var convnetjs = require('convnetjs');

var readCSV = Promise.promisify(basicCsv.readCSV);

var argv = (index, defaultValue) =>
	(process.argv[index] != undefined)? process.argv[index]: defaultValue;

var epoch = parseInt( argv(2, '50') );
var trainRate = parseFloat( argv(3, '0.7') );

var batchSize = parseInt( argv(4, '1') );
var learningRate = parseFloat( argv(5, '0.01') );
var updateMethod = argv(6, 'sgd');

var fcNeuNum = parseInt( argv(7, '6') );
var fcAct = argv(8, 'relu');

var categoryMap = {
	'Iris-setosa': 0,
	'Iris-versicolor': 1,
	'Iris-virginica': 2
};

var layer_defs = [
	{ type: 'input', out_sx: 1, out_sy: 1, out_depth: 4 },
	{ type: 'fc', num_neurons: fcNeuNum, activation: fcAct },
	{ type: 'softmax', num_classes: 3 }
]

var net = new convnetjs.Net();
net.makeLayers(layer_defs);

var trainer = new convnetjs.Trainer(net, {
	batch_size: batchSize,
	learning_rate: learningRate,
	method: updateMethod
});

var batchLearn = (data, learnLossFunc) => {
	var res = data.reduce(
		(acc, d) => {
			acc.loss += learnLossFunc(d);
			acc.accuracy += net.getPrediction() == d.label? 1: 0;

			return acc;
		},
		{ loss: 0.0, accuracy: 0 }
	);

	for (var key in res) {
		res[key] /= data.length;
	}

	return res;
};

readCSV('iris.data')
	.then( ds => 
		ds.map(d => 
			new Object({
				features: new convnetjs.Vol([ d[0], d[1], d[2], d[3] ]),
				label: categoryMap[d[4]]
			})
		)
	)
	.then( ds => {
		shuffle(ds);

		var trainSize = Math.floor(ds.length * trainRate);
		var testData = ds.splice(trainSize);

		return {train: ds, test: testData};
	})
	.then( data => {
		for (var i = 0; i < epoch; i++) {
			var trainData = shuffle(data.train, {copy: true});

			var trainRes = batchLearn(trainData, d => 
				trainer.train(d.features, d.label).loss
			);

			var testRes = batchLearn(data.test, d => {

				net.forward(d.features);

				return net.backward(d.label);
			});

			console.log([
				trainRes.loss, trainRes.accuracy, 
				testRes.loss, testRes.accuracy
			].join(','));
		}
	});
