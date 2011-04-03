
var mongoose = require('mongoose');

var UserSchema = new mongoose.Schema({
	name: String
});

var CommentSchema = new mongoose.Schema({
	content: String,
	created_date: Date,
	user_id: mongoose.Schema.ObjectId
	//ˆÈ‰º‚Å‚à‰Â
	//user_id: {}
});

CommentSchema.virtual('user')
	.get(function() {
		return this['userobj'];
	})
	.set(function(u) {
		this.set('user_id', u._id)
	});

var BookSchema = new mongoose.Schema({
	title: String,
	isbn: String,
	comments: [CommentSchema]
});

BookSchema.method({
	//Comment ‚ÌŠÖ˜A User ‚ğƒ[ƒh‚·‚éˆ—
	deepLoad: function(callback) {
		var comments = this.get('comments');
		var ulist = [];

		for (var i = 0; i < comments.length; i++) {
			var uid = comments[i].user_id;
			ulist.push(uid);
		}

		var thisObj = this;

		User.where('_id').in(ulist).find(function(err, list) {
			var userList = {};

			if (!err) {
				for (var i = 0; i < list.length; i++) {
					userList[list[i]._id] = list[i].doc;
				}
			}

			for (var i = 0; i < comments.length; i++) {
				comments[i]['userobj'] = userList[comments[i].user_id];
			}

			callback(thisObj);
		});
	}
});

mongoose.model('User', UserSchema);
mongoose.model('Book', BookSchema);

exports.createConnection = function(url) {
	return mongoose.createConnection(url);
};

/*
var db = mongoose.createConnection('mongodb://127.0.0.1/book_review');

var User = db.model('User');
var Book = db.model('Book');
*/
