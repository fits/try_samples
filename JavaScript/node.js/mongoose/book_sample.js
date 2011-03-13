
var mongoose = require('mongoose');

var UserSchema = new mongoose.Schema({
	name: String
});

var CommentSchema = new mongoose.Schema({
	content: String,
	created_date: Date,
	user_id: mongoose.Schema.ObjectId
	//以下でも可
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
	//Comment の関連 User をロードする処理
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

var db = mongoose.createConnection('mongodb://127.0.0.1/book_review');

var User = db.model('User');
var Book = db.model('Book');

/*
var u = new User({name: 'tester3'});
u.save();

var b = new Book({title: 'test2'});
b.comments.push({content: 'test3 data', created_date: Date.now(), user: u});
b.comments.push({content: 'aaaa', created_date: Date.now(), user: u});

b.save(function(err) {
	console.log("saved : " + err);
});
*/

Book.where('comments.content', 'test3 data').find(function(err, list) {

	for (var i = 0; i < list.length; i++) {
		var l = list[i];

		l.deepLoad(function(b) {
		// 常に l の値が list 配列の最後の要素になってしまうため、
		// 以下のようにすると意図したように動作しない
		//	var b = l.doc;

			console.log("--- " + b.title + " ---");
			var comments = b.comments;

			for (var j = 0; j < comments.length; j++) {
				var c = comments[j];
				var cname = (c.user)? c.user.name: null;

				console.log(c.content + ", " + c.user_id + ", " + cname);
			}
		});
	}
});

