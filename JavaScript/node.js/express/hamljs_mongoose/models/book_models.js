
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
	//Comment ‚ÌŠÖ˜A User ‚ğ•œŒ³‚·‚éˆ—
	restoreUser: function(userList) {
		var ulist = {};
		for (var i = 0; i < userList.length; i++) {
			ulist[userList[i]._id] = userList[i];
		}

		var comments = this.get('comments');

		for (var i = 0; i < comments.length; i++) {
			comments[i]['userobj'] = ulist[comments[i].user_id];
		}
	}
});

mongoose.model('User', UserSchema);
mongoose.model('Book', BookSchema);

exports.createConnection = function(url) {
	return mongoose.createConnection(url);
};
