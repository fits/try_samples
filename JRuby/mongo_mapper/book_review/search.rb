require "rubygems"
require "mongo_mapper"

require "models/book"
require "models/user"
require "models/comment"

# MongoMapper settings
MongoMapper.connection = Mongo::Connection.new('localhost')
MongoMapper.database = 'book_review'

Book.where('comments.content' => 'test1').sort(:title).each do |b|

	puts "--- #{b.title} ---"

	b.comments.each do |c|
		puts "#{c.content}, #{c.user_id} - #{c.user.name}"
	end
end

