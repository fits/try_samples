'use strict';

const levelup = require('levelup');

const db = levelup('./db');

db.createReadStream()
	.on('data', d => console.log(`key = ${d.key}, value = ${d.value}`))
	.on('error', e => console.error(e));
