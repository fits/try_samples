
var mongoose = require('mongoose');

var UserSchema = new mongoose.Schema({
	name: String
});

var CommentSchema = new mongoose.Schema({
	content: String,
	created_date: Date,
	//user_id Ç ObjectId å^Ç…Ç∑ÇÈÇ…ÇÕ type Çè»ó™Ç∑ÇÍÇŒÇÊÇ¢
	user_id: {}
});

CommentSchema.method({
	user: function() {
		return User.findById(this.user_id);
	}
});

var BookSchema = new mongoose.Schema({
	title: String,
	isbn: String,
	comments: [CommentSchema]
});

mongoose.model('User', UserSchema);
mongoose.model('Book', BookSchema);

var db = mongoose.createConnection('mongodb://127.0.0.1/book_review');

var User = db.model('User');
var Book = db.model('Book');

var u = new User({name: 'tester1'});
u.save();

var b = new Book({title: 'test'});
b.comments.push({content: 'test data', created_date: Date.now(), user_id: u._id});

b.save(function(err) {
	console.log("saved : " + err);

	db.close();
});

