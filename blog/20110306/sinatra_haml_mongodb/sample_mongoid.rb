require "rubygems"
require "sinatra"
require "haml"
require "mongoid"

require "models_mongoid/book"
require "models_mongoid/user"
require "models_mongoid/comment"

# Mongoid settings
Mongoid.configure do |config|
	config.master = Mongo::Connection.new.db("book_review")
end

# Top Page
get '/' do
	haml :index, {}, :books => Book.all.order_by([[:title, :asc]]), :users => User.all.order_by([[:name, :asc]]), :action => '/comments'
end

# Book List Page
get '/books' do
	haml :book, {}, :books => Book.all.order_by([[:title, :asc]]), :action => '/books'
end

# User List Page
get '/users' do
	haml :user, {}, :users => User.all.order_by([[:name, :asc]]), :action => '/users'
end


# Add Book
post '/books' do
	Book.create(params[:post])
	redirect '/books'
end

# Add Comment
post '/comments' do
	b = Book.find(params[:post][:book_id])
	b.comments << Comment.new(:content => params[:post][:content], :created_date => Time.now, :user_id => params[:post][:user_id])
	b.save

	#以下でも可
	# Book.find(params[:post][:book_id]).comments.create(:content => params[:post][:content], :created_date => Time.now, :user_id => params[:post][:user_id])

	redirect '/'
end

# Add User
post '/users' do
	User.create(params[:post])
	redirect '/users'
end
