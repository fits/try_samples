var http = require('http');
var fs = require('fs');
var path = require('path');

var dir = process.argv[2];
var trgUrl = require('url').parse(process.argv[3]);
trgUrl.path = trgUrl.pathname;

console.log(trgUrl);

http.get(trgUrl, function(res) {
	res.setEncoding('binary');
	var buf = '';

	res.on('data', function(chunk) {
		console.log(chunk.length);
		buf += chunk;
	});

	res.on('end', function() {
		console.log("end");
		fs.writeFile(path.join(dir, path.basename(trgUrl.path)), buf, 'binary', function(err) {
			if (err) throw err;
			console.log('downloaded:');
		});
	});
});

