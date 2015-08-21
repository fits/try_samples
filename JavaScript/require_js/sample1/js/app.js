
require(['sample'], function(sample) {
	var res = sample.proc('hello');

	document.getElementById('msg').innerHTML = res;

	console.log(res);
});

