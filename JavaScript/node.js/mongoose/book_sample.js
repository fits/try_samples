
var mongoose = require('mongoose');

var CommentModel = new mongoose.Schema({
	content: String,
	created_date: Date
});

var BookModel = new mongoose.Schema({
	title: String,
	isbn: String,
	comments: [CommentModel]
});

mongoose.model('Book', BookModel);

var db = mongoose.createConnection('mongodb://127.0.0.1/book_review');

var Book = db.model('Book');

var b = new Book({title: 'test'});
b.comments.push({content: 'test data', created_date: Date.now()});

b.save(function(err) {
	console.log("saved : " + err);

	db.close();
});

