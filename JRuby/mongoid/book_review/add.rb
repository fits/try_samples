require "rubygems"
require "mongoid"

require "models/book"
require "models/user"
require "models/comment"

Mongoid.configure do |config|
	config.master = Mongo::Connection.new.db("book_review")
	#ƒL[‚ÌŒ^‚ð BSON::ObjectId ‚É‚·‚é‚½‚ß‚ÌÝ’è
	#
	#‰º‹L‚ð—LŒø‚É‚·‚é‚Æ belongs_to ‚ðŠÜ‚Þ Model ‚ÌŒŸõ‚ÅŽ¸”s‚·‚é‚æ‚¤‚É‚È‚é
	#ibelongs_to ‚Å•Û‘¶‚³‚ê‚é xxx_id ‚ÌŒ^‚ª String ‚Ì‚½‚ßj
	#config.use_object_ids = true
end

u1 = User.create(:name => 'fits')
u2 = User.create(:name => 'tester')

b1 = Book.new(:title => 'Rails')
b1.comments << Comment.new(:content => 'test1', :created_date => Time.now, :user => u1)
b1.comments << Comment.new(:content => 'test2', :created_date => Time.now, :user => u2)
b1.save

b2 = Book.new(:title => 'Hadoop')
b2.comments << Comment.new(:content => 'test1', :created_date => Time.now, :user => u1)
b2.save

Book.create(:title => 'nnode.js')

