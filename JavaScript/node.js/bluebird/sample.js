
var Promise = require('bluebird');

var f = function(v, callback) {
	if (v > 0) {
		callback(null, v);
	}
	else {
		callback('error', null);
	}
};

f(5, (err, res) => console.log(res));

f(-1, (err, res) => console.error(err));

var pf = Promise.promisify(f);

pf(5).then(r => console.log(r));
pf(-1).error(r => console.log(r));

var s = function(v) {
	return Promise.promisify(f)(v).then(r => r * 10);
};

s(5).then(r => console.log(r));
s(-1).error(r => console.log(r));

Promise.all([
	pf(3),
	pf(2)
]).spread( (r1, r2) => console.log(r1 * r2) );
