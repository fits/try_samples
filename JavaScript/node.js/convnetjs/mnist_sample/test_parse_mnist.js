
var mnist = require('./parse_mnist');

var printData = d => {
	console.log(`***** number = ${d.label} *****`);

	var v = d.values;

	for (var y = 0; y < v.sy; y++) {
		var r = Array(v.sx);

		for (var x = 0; x < v.sx; x++) {
			r[x] = v.get(x, y, 0) > 0 ? '#' : ' ';
		}

		console.log(r.join(''));
	}
};

mnist.parse('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')
	.then(ds => {
		console.log(`size: ${ds.length}`);

		printData(ds[0]);
		printData(ds[1]);
	});
