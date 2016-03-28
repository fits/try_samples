'use strict';

const Promise = require('bluebird');
const convnetjs = require('convnetjs');

const fs = require('fs');

const readFile = Promise.promisify(fs.readFile);

const readToBuffer = file => readFile(file).then(r => new Buffer(r, 'binary'));

const loadImages = file =>
	readToBuffer(file)
		.then(buf => {
			const magicNum = buf.readInt32BE(0);

			const num = buf.readInt32BE(4);
			const rowNum = buf.readInt32BE(8);
			const colNum = buf.readInt32BE(12);

			const dataBuf = buf.slice(16);

			const res = Array(num);

			let offset = 0;

			for (let i = 0; i < num; i++) {
				const data = new convnetjs.Vol(colNum, rowNum, 1, 0);

				for (let y = 0; y < rowNum; y++) {
					for (let x = 0; x < colNum; x++) {

						const value = dataBuf.readUInt8(offset++);

						data.set(x, y, 0, value);
					}
				}

				res[i] = data;
			}

			return res;
		});

const loadLabels = file =>
	readToBuffer(file)
		.then(buf => {
			const magicNum = buf.readInt32BE(0);

			const num = buf.readInt32BE(4);

			const dataBuf = buf.slice(8);

			const res = Array(num);

			for (let i = 0; i < num; i++) {
				res[i] = dataBuf.readUInt8(i);
			}

			return res;
		});

module.exports.loadMnist = (imgFile, labelFile) => 
	Promise.all([
		loadImages(imgFile),
		loadLabels(labelFile)
	]).spread( (r1, r2) => 
		r2.map((label, i) => new Object({ values: r1[i], label: label }))
	);

