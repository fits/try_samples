
var Promise = require('bluebird');
var convnetjs = require('convnetjs');

var fs = require('fs');

var readFile = Promise.promisify(fs.readFile);

var readToBuffer = file => readFile(file).then(r => new Buffer(r, 'binary'));

var loadImages = file =>
	readToBuffer(file)
		.then(buf => {
			var magicNum = buf.readInt32BE(0);

			var num = buf.readInt32BE(4);
			var rowNum = buf.readInt32BE(8);
			var colNum = buf.readInt32BE(12);

			var dataBuf = buf.slice(16);

			var res = Array(num);

			var offset = 0;

			for (var i = 0; i < num; i++) {
				var data = new convnetjs.Vol(colNum, rowNum, 1, 0);

				for (var y = 0; y < rowNum; y++) {
					for (var x = 0; x < colNum; x++) {

						var value = dataBuf.readUInt8(offset++);

						data.set(x, y, 0, value);
					}
				}

				res[i] = data;
			}

			return res;
		});

var loadLabels = file =>
	readToBuffer(file)
		.then(buf => {
			var magicNum = buf.readInt32BE(0);

			var num = buf.readInt32BE(4);

			var dataBuf = buf.slice(8);

			var res = Array(num);

			for (var i = 0; i < num; i++) {
				res[i] = dataBuf.readUInt8(i);
			}

			return res;
		});

module.exports.loadMnist = (imgFile, labelFile) => 
	Promise.all([
		loadImages(imgFile),
		loadLabels(labelFile)
	]).spread( (r1, r2) => 
		r2.map((label, i) => {
			return { values: r1[i], label: label };
		})
	);

