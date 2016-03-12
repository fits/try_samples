"use strict";

const Promise = require('bluebird');
const fs = require('fs');
const convnetjs = require('convnetjs');

const readFile = Promise.promisify(fs.readFile);

const readToBuffer = file => readFile(file).then(r => new Buffer(r, 'binary'));

module.exports.parseLabels = file =>
	readToBuffer(file)
		.then(buf => {
			const num = buf.readInt32BE(4);

			const dataBuf = buf.slice(8);

			const res = Array(num);

			for (let i = 0; i < num; i++) {
				res[i] = dataBuf.readUInt8(i);
			}

			return res;
		});

module.exports.parseImages = file =>
	readToBuffer(file)
		.then(buf => {
			const num = buf.readInt32BE(4);
			const rowNum = buf.readInt32BE(8);
			const colNum = buf.readInt32BE(12);

			const dataBuf = buf.slice(16);

			const res = Array(num);

			for (let i = 0; i < num; i++) {
				const values = new convnetjs.Vol(colNum, rowNum, 1, 0);
				res[i] = values;

				for (let y = 0; y < rowNum; y++) {
					for (let x = 0; x < colNum; x++) {
						const value = dataBuf.readUInt8(x + (y * colNum) + (i * colNum * rowNum));
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
		r2.map((label, i) => new Object({ label: label, values: r1[i] }))
	);
