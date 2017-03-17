'use strict'

const Seneca = require('seneca');

Seneca()
	.add({cmd: 'sample'}, (msg, done) => {
		console.log(msg);

		const res = `=== ${msg.data} ===`;

		done(null, { res: res });
	})
	.listen({type: 'http', port: '8080', pin: 'cmd:*'});
