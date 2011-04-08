var express = require('express');
var app = express.createServer();

/*
 *  post でパラメータを取得するのに必要な設定
 *  bodyDecoder を設定しないと request.body が undefined になり、
 *  post された値が request.body や param() 等で取得できない
 */
app.use(express.bodyDecoder());

//haml.js の設定
app.register('.haml', require('hamljs'));
//view engine に haml を設定することで render で index.haml のように
//拡張子を指定しなくてもよくなる
app.set('view engine', 'haml');

//モデルクラス定義の呼び出し
var mongoModel = require('./models/book_models');
//MongoDB への接続設定
var db = mongoModel.createConnection('mongodb://127.0.0.1/book_review');
//Book モデルを取得
var Book = db.model('Book');
//User モデルを取得
var User = db.model('User');

//Top ページ表示
app.get('/', function(req, res) {
	User.find().asc('name').find(function(err, userList) {
		Book.find().asc('title').find(function(err, bookList) {

			bookList.forEach(function(b) {
				//Comment の関連 User を復元
				b.restoreUser(userList);
			});

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

//Book ページ表示
app.get('/books', function(req, res) {
	Book.find().asc('title').find(function(err, list) {
		res.render('book', {
			locals: {
				books: list,
				action: '/books'
			}
		});
	});
});

//Book 追加処理
app.post('/books', function(req, res) {
	new Book(req.body).save(function(err) {
		res.redirect('/books');
	});
});

//Comment 追加処理
app.post('/comments', function(req, res) {
	User.findById(req.body.user_id, function(err, u) {
		Book.findById(req.body.book_id, function(err, b) {
			//Comment 追加
			b.comments.push({
				content: req.body.content,
				created_date: Date.now(),
				user: u
			});
			//保存
			b.save(function(err) {
				res.redirect('/');
			});
		});
	});
});

//User ページ表示
app.get('/users', function(req, res) {
	User.find().asc('name').find(function(err, list) {
		res.render('user', {
			locals: {
				users: list,
				action: '/users'
			}
		});
	});
});

//User 追加処理
app.post('/users', function(req, res) {
	new User(req.body).save(function(err) {
		res.redirect('/users');
	});
});

//ポート 8081 で Web サーバーを起動
app.listen(8081);
