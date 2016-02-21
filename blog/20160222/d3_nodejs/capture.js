var system = require('system');

var webFile = system.args[1];
var pngFile = system.args[2];

var page = require('webpage').create();

page.open(webFile, function(status){
	if (status == 'success') {
		page.render(pngFile);
	}

	phantom.exit();
});
