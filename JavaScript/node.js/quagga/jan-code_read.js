
var Quagga = require('quagga');

Quagga.decodeSingle(
	{
		src: process.argv[2],
		numOfWorkers: 0,
		decoder: {
			readers: ['ean_reader']
		}
	},
	(res) => {
		if (res.codeResult) {
			console.log(`code: ${res.codeResult.code}`);
		}
		else {
			console.log('failed');
		}
	}
);

