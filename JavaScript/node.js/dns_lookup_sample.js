
require('dns').lookup("www.google.com", function(err, ip, addressType) {
	console.log(err + ", " + ip + ", " + addressType);
});

