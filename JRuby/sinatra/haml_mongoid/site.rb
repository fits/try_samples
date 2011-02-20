require "rubygems"
require "sinatra"
require "haml"
require "mongoid"

require "models/book"
require "models/comment"
require "models/user"

# Mongoid settings
Mongoid.configure do |config|
	config.master = Mongo::Connection.new.db("book_review")
end

get '/' do
	books = Book.all
	haml :index, {}, :books => books
end

post '/books' do
	Book.create(params[:post])
end

get '/users' do
	users = User.all
	haml :user, {}, :users => users
end

post '/users' do
	User.create(params[:post])
	redirect '/users'
end
