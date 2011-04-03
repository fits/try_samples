var express = require('express');

var app = express.createServer();

app.register('.haml', require('hamljs'));
app.set('view engine', 'haml');

app.get('/', function(req, res) {
	res.render('index', {
		locals: {
			books: [
				{title: 'aaa', isbn: '', comments: []}
			],
			action: '/comments'
		}
	});
});

app.get('/books', function(req, res) {
	res.render('book', {
		locals: {
			books: [
				{title: 'test1', isbn: '0001'},
				{title: 'test2', isbn: '0002'},
				{title: 'test3', isbn: '0003'}
			],
			action: '/books'
		}
	});
});

app.post('/books', function(req, res) {
	
	res.redirect('/books');
});


app.listen(8081);
