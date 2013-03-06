var system = require('system');

if (system.args.length < 2) {
	console.log('phantomjs ' + system.args[0] + ' <url>');
	phantom.exit();
}

var page = require('webpage').create();

page.open(system.args[1], function(status) {
	if (status == 'success') {
		var body = page.evaluate(function() {
			return document.body.textContent;
		});

		console.log("body: " + body);
	}

	phantom.exit();
});
