var mongoose = require('mongoose');

//Userスキーマ定義
var UserSchema = new mongoose.Schema({
	name: String
});

//Commentスキーマ定義
var CommentSchema = new mongoose.Schema({
	content: String,
	created_date: Date,
	user_id: mongoose.Schema.ObjectId
});

//user のアクセサメソッド定義
CommentSchema.virtual('user')
	.get(function() {
		return this['userobj'];
	})
	.set(function(u) {
		this.set('user_id', u._id)
	});

//Book スキーマ定義
var BookSchema = new mongoose.Schema({
	title: String,
	isbn: String,
	comments: [CommentSchema]
});

//Book へのメソッド追加
BookSchema.method({
	//Comment の関連 User を復元する処理
	restoreUser: function(userList) {
		var ulist = {};

		userList.forEach(function(u) {
			ulist[u._id] = u;
		});

		this.get('comments').forEach(function(c) {
			c['userobj'] = ulist[c.user_id];
		});
	}
});

//User モデルの定義
mongoose.model('User', UserSchema);
//Book モデルの定義
mongoose.model('Book', BookSchema);

//接続メソッドの定義
exports.createConnection = function(url) {
	return mongoose.createConnection(url);
};
