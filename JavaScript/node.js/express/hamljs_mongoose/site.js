var express = require('express');
var app = express.createServer();

//post でパラメータを取得するのに必要
//bodyDecoder の設定が無いと request.body が undefined になる
app.use(express.bodyDecoder());

//haml.js の設定
app.register('.haml', require('hamljs'));
app.set('view engine', 'haml');

var model = require('./models/book_models');
var db = model.createConnection('mongodb://127.0.0.1/book_review');
var Book = db.model('Book');
var User = db.model('User');

app.get('/', function(req, res) {
	Book.find(function(err, bookList) {
		User.find(function(err, userList) {
			res.render('index', {
				locals: {
					books: bookList,
					users: userList,
					action: '/comments'
				}
			});
		});
	});
});

app.get('/books', function(req, res) {
	Book.find(function(err, list) {
		res.render('book', {
			locals: {
				books: list,
				action: '/books'
			}
		});
	});
});

app.post('/books', function(req, res) {
	new Book(req.body).save(function(err) {
		res.redirect('/books');
	});
});

app.get('/users', function(req, res) {
	User.find(function(err, list) {
		res.render('user', {
			locals: {
				users: list,
				action: '/users'
			}
		});
	});
});

app.post('/users', function(req, res) {
	new User(req.body).save(function(err) {
		res.redirect('/users');
	});
});

app.listen(8081);
