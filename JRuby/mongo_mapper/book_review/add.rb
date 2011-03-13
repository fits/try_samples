require "rubygems"
require "mongo_mapper"

require "models/book"
require "models/user"
require "models/comment"

# MongoMapper settings
MongoMapper.connection = Mongo::Connection.new('localhost')
MongoMapper.database = 'book_review'

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
