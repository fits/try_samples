
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
		return this.get('userobj');
	/*
		var result = null;

		User.findById(this.user_id, function(err, item) {
			if (!err) {
				result = item.doc;
			}
		});

		//findById ‚ª”ñ“¯Šúˆ—‚Ì‚½‚ß null ‚ª•Ô‚Á‚Ä‚µ‚Ü‚¤
		return result;
	*/
	})
	.set(function(u) {
		this.set("user_id", u._id)
	});

//‰Šú‰»Œã‚Ìˆ—
CommentSchema.post('init', function() {
	this.initCompleted = function(fn) {
		if (this.get('userobj') == null) {
			this.completedFunc = fn;
		}
		else {
			fn();
		}
	};
	
	var thisObj = this;

	User.findById(this.user_id, function(err, item) {
		thisObj.set('userobj', item.doc);

		if (thisObj.completedFunc) {
			thisObj.completedFunc();
		}
	});
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

var u = new User({name: 'tester4'});
u.save();

var b = new Book({title: 'test4'});
b.comments.push({content: 'test4 data', created_date: Date.now(), user: u});
b.comments.push({content: 'aaaa', created_date: Date.now(), user: u});

b.save(function(err) {
	console.log("saved : " + err);
});

Book.where('comments.content', 'test3 data').find(function(err, list) {

	for (var i = 0; i < list.length; i++) {
		var doc = list[i].doc;

		for (var j = 0; j < doc.comments.length; j++) {
			var c = doc.comments[j];

			c.initCompleted(function() {
				console.log(doc.title + ", " + c.content + ", " + c.user.name);
			});
		}
	}
});

Book.find({'comments.content' : 'test3 data'}, function(err, list) {
	for (var i = 0; i < list.length; i++) {
		var doc = list[i].doc;

		for (var j = 0; j < doc.comments.length; j++) {
			var c = doc.comments[j];

			c.initCompleted(function() {
				console.log(doc.title + ", " + c.content + ", " + c.user.name);
			});
		}
	}

	//db.close();
});

