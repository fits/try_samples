
var Promise = require('bluebird');
var fs = require('fs');
var convnetjs = require('convnetjs');

var readFile = Promise.promisify(fs.readFile);

var readToBuffer = file => readFile(file).then(r => new Buffer(r, 'binary'));

module.exports.parseLabels = file =>
	readToBuffer(file)
		.then(buf => {
			var num = buf.readInt32BE(4);

			var dataBuf = buf.slice(8);

			var res = Array(num);

			for (var i = 0; i < num; i++) {
				res[i] = dataBuf.readUInt8(i);
			}

			return res;
		});

module.exports.parseImages = file =>
	readToBuffer(file)
		.then(buf => {
			var num = buf.readInt32BE(4);
			var rowNum = buf.readInt32BE(8);
			var colNum = buf.readInt32BE(12);

			var dataBuf = buf.slice(16);

			var res = Array(num);

			for (var i = 0; i < num; i++) {
				var values = new convnetjs.Vol(colNum, rowNum, 1, 0);
				res[i] = values;

				for (var y = 0; y < rowNum; y++) {
					for (var x = 0; x < colNum; x++) {
						var value = dataBuf.readUInt8(x + (y * colNum) + (i * colNum * rowNum));
						values.set(x, y, 0, value);
					}
				}
			}

			return res;
		});

module.exports.parse = (imgFile, labelFile) => 
	Promise.all([
		module.exports.parseImages(imgFile),
		module.exports.parseLabels(labelFile)
	]).spread( (r1, r2) => 
		r2.map((label, i) => {
			return { label: label, values: r1[i] };
		})
	);
