'use strict';

const act = process.argv[2];
const numNeurons = parseInt(process.argv[3]);
const jsonDestFile = process.argv[4];

require('./save_model').saveModel(
	[
		{ type: 'input', out_sx: 28, out_sy: 28, out_depth: 1 },
		{ type: 'fc', activation: act, num_neurons: numNeurons },
		{ type: 'softmax', num_classes: 10 }
	], 
	jsonDestFile
);
