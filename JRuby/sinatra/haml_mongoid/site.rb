require "rubygems"
require "sinatra"
require "haml"
require "mongoid"

require "models/book"

# Mongoid settings
Mongoid.configure do |config|
	config.master = Mongo::Connection.new.db("book_review")
end

get '/' do
	books = Book.where
	haml :index, {}, :books => books
end

get '/new/:title' do
	Book.create(params)
end

