"use strict";

const Promise = require('bluebird');
const convnetjs = require('convnetjs');

const writeFile = Promise.promisify(require('fs').writeFile);

module.exports.saveModel = (layers, destFile) => {

	let net = new convnetjs.Net();
	net.makeLayers(layers);

	writeFile(destFile, JSON.stringify(net.toJSON()))
		.error( e => console.error(e) );
};