
var mnist = require('./load_mnist');

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

	console.log(d.values.w.join(','));
};

mnist.loadMnist(process.argv[2], process.argv[3])
	.then(ds => {
		console.log(`size: ${ds.length}`);

		printData(ds[0]);

		console.log('----------');

		printData(ds[1]);
	});
