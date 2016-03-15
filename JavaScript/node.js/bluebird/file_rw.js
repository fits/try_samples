"use strict";

const Promise = require('bluebird');
const fs = require('fs');

const readFile = Promise.promisify(fs.readFile);
const writeFile = Promise.promisify(fs.writeFile);

readFile('input.json')
	.then( data => JSON.parse(data) )
	.then( obj => {
		obj['sa1'] = 'abc';
		obj['n'] = 101;

		return obj;
	})
	.then( obj => JSON.stringify(obj) )
	.then( json => writeFile('output.json', json) )
	.then( () => console.log('done') )
	.catch( e => console.error(e) );
