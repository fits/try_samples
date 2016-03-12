"use strict";

require('./save_model').saveModel(
	[
		{ type: 'input', out_sx: 28, out_sy: 28, out_depth: 1 },
		{ type: 'fc', activation: 'relu', num_neurons: 1000 },
		{ type: 'softmax', num_classes: 10 }
	],
	process.argv[2]
);
