var express = require('express');

var app = express.createServer();

app.get('/', function(req, res) {
	res.send('test data');
});

app.listen(8081);
