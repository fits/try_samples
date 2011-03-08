
var mongoose = require('mongoose');

var BookModel = new mongoose.Schema({
	title: String,
	isbn: String
});

mongoose.model('Book', BookModel);

var db = mongoose.createConnection('mongodb://127.0.0.1/book_review');

var Book = db.model('Book');

new Book({title: 'test'}).save(function(err) {
	console.log("saved : " + err);

	db.close();
});

