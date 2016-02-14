
var Promise = require('bluebird');
var basicCsv = require('basic-csv');
var shuffle = require('shuffle-array');
var convnetjs = require('convnetjs');

var readCSV = Promise.promisify(basicCsv.readCSV);

var epoch = 20;
var trainRate = 0.7;

var categoryMap = {
	'Iris-setosa': 0,
	'Iris-versicolor': 1,
	'Iris-virginica': 2
};

// 階層型ニューラルネットの構成
var layer_defs = [
	{ type: 'input', out_sx: 1, out_sy: 1, out_depth: 4 },
	{ type: 'softmax', num_classes: 3 }
];

var model = new convnetjs.Net();
model.makeLayers(layer_defs);

var trainer = new convnetjs.Trainer(model);

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
			// 学習
			data.train.forEach( d => trainer.train(d.features, d.label) );

			// 評価
			var testAccSum = data.test.reduce( (acc, d) => {
				model.forward(d.features);

				return acc + (model.getPrediction() == d.label? 1: 0);
			}, 0);

			console.log(testAccSum / data.test.length);
		}
	});
