
if (process.argv.length < 3) {
	console.log("node dns_lookup_sample.js [host]");
	return;
}

var host = process.argv[2];

require('dns').lookup(host, function(err, ip, addressType) {
	console.log(err + ", " + ip + ", " + addressType);
});

