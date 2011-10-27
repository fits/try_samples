process.stdin.resume();

process.stdin.on('data', function(chunk) {
	chunk.toString().trim().split('\n').forEach(function(d) {
		console.log('data : ' + d);
	});
});

