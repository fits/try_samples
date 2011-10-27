
process.stdin.resume();

process.stdin.on('data', function(chunk) {
	console.log('data : ' + chunk);
});

process.stdin.on('end', function() {
	console.log('end');
});

