
var mongoose = require('mongoose');


var Book = new mongoose.Schema({
	title: String,
	isbn: String
});

var db = mongoose.createConnection('127.0.0.1', 'book_review');



db.close();

