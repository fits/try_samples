var express = require('express');

var app = express.createServer();

app.register('.haml', require('hamljs'));
app.set('view engine', 'haml');

app.get('/', function(req, res) {
	res.render('index', {data: 'test data'});
});

app.listen(8081);
