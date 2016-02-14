var system = require('system');

if (system.args.length < 3) {
	console.log("<url> <dest file>");
	phantom.exit();
}

var page = require('webpage').create();

page.open(system.args[1], function(status){
	if (status == 'success') {
		page.render(system.args[2]);
	}

	phantom.exit();
});