var express = require('express');

var app = express.createServer();

app.register('.haml', require('hamljs'));
app.set('view engine', 'haml');

app.get('/', function(req, res) {
	res.render('index', {
		locals: {
			items: [
				{id: 1, data: 'test1'},
				{id: 2, data: 'test2'},
				{id: 3, data: 'test3'}
			]
		}
	});
});

app.listen(8081);
