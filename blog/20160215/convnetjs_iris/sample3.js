
var Promise = require('bluebird');
var basicCsv = require('basic-csv');
var shuffle = require('shuffle-array');
var convnetjs = require('convnetjs');

var readCSV = Promise.promisify(basicCsv.readCSV);

var epoch = 50;
var trainRate = 0.7;
var batchSize = 1;
var learningRate = parseFloat(process.argv[2]);
var updateMethod = process.argv[3];

var fcNeuNum = parseInt(process.argv[4]); // 隠れ層のニューロン数
var fcAct = process.argv[5]; // 隠れ層の活性化関数

var categoryMap = {
	'Iris-setosa': 0,
	'Iris-versicolor': 1,
	'Iris-virginica': 2
};

// 構成
var layer_defs = [
	{ type: 'input', out_sx: 1, out_sy: 1, out_depth: 4 },
	{ type: 'fc', num_neurons: fcNeuNum, activation: fcAct },
	{ type: 'softmax', num_classes: 3 }
];

var model = new convnetjs.Net();
model.makeLayers(layer_defs);

var trainer = new convnetjs.Trainer(model, {
	batch_size: batchSize,
	learning_rate: learningRate,
	method: updateMethod
});

// 学習・評価の実施
var batchLearn = (data, learnLossFunc) => {
	var res = data.reduce(
		(acc, d) => {
			// 誤差の加算
			acc.loss += learnLossFunc(d);
			// 正解数の加算
			acc.accuracy += (model.getPrediction() == d.label)? 1: 0;

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
			shuffle(data.train);

			// 学習
			var trainRes = batchLearn(data.train, d => 
				trainer.train(d.features, d.label).loss
			);

			// テスト（評価）
			var testRes = batchLearn(data.test, d => {

				model.forward(d.features);

				return model.backward(d.label);
			});

			console.log([
				trainRes.loss, trainRes.accuracy, 
				testRes.loss, testRes.accuracy
			].join(','));
		}
	});
