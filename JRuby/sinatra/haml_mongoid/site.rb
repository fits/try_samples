require "rubygems"
require "sinatra"
require "haml"
require "mongoid"

require "models/book"
require "models/user"
require "models/comment"


# Mongoid settings
Mongoid.configure do |config|
	config.master = Mongo::Connection.new.db("book_review")
end

get '/' do
	haml :index, {}, :books => Book.all.order_by([[:name, :asc]]), :users => User.all, :action => '/comments'
end

get '/books' do
	haml :book, {}, :books => Book.all, :action => '/books'
end

post '/books' do
	Book.create(params[:post])
	redirect '/books'
end

post '/comments' do
	user = User.find(params[:post][:user])
	comment = Book.find(params[:post][:book]).comments.create(:content => params[:post][:content], :created_date => Time.now, :user => user)

	redirect '/'
end

get '/users' do
	haml :user, {}, :users => User.all, :action => '/users'
end

post '/users' do
	User.create(params[:post])
	redirect '/users'
end
