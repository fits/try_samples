var http = require('http');
var fs = require('fs');
var path = require('path');

var dir = process.argv[2];
var urlString = process.argv[3];

var trgUrl = require('url').parse(urlString);
trgUrl.path = trgUrl.pathname;

console.log(trgUrl);

var printError = function(e) {
	console.log('failed: ' + urlString + ', ' + e.message);
}

http.get(trgUrl, function(res) {
	res.setEncoding('binary');
	var buf = '';

	res.on('data', function(chunk) {
		console.log(chunk.length);
		buf += chunk;
	});

	res.on('end', function() {
		console.log("end");
		var filePath = path.join(dir, path.basename(trgUrl.path));

		fs.writeFile(filePath, buf, 'binary', function(err) {
			if (err) {
				printError(err);
			}
			else {
				console.log('downloaded: ' + urlString + ' => ' + filePath);
			}
		});
	});

	res.on('close', function(err) {
		if (err) {
			printError(err);
		}
	});

}).on('error', function(err) {
	printError(err);
});

