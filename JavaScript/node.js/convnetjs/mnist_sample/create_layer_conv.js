"use strict";

require('./save_model').saveModel(
	[
		{ type: 'input', out_sx: 28, out_sy: 28, out_depth: 1 },
		{ type: 'conv', sx: 5, filters: 8, stride: 1, pad: 2, activation: 'relu' },
		{ type: 'pool', sx: 2, stride: 2 },
		{ type: 'conv', sx: 5, filters: 16, stride: 1, pad: 2, activation: 'relu' },
		{ type: 'pool', sx: 3, stride: 3 },
		{ type: 'softmax', num_classes: 10 }
	],
	process.argv[2]
);
