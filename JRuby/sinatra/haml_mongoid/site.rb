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

get '/books' do
	haml :book, {}, :books => Book.all, :action => '/books'
end

post '/books' do
	Book.create(params[:post])
	redirect '/books'
end

get '/users' do
	haml :user, {}, :users => User.all, :action => '/users'
end

post '/users' do
	User.create(params[:post])
	redirect '/users'
end
