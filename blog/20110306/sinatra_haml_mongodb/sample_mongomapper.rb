require "rubygems"
require "sinatra"
require "haml"
require "mongo_mapper"

require "models_mongomapper/book"
require "models_mongomapper/user"
require "models_mongomapper/comment"

# MongoMapper settings
MongoMapper.connection = Mongo::Connection.new('localhost')
MongoMapper.database = 'book_review'

# Top Page
get '/' do
	haml :index, {}, :books => Book.all(:order => 'title'), :users => User.all(:order => 'name'), :action => '/comments'
end

# Book List Page
get '/books' do
	haml :book, {}, :books => Book.all(:order => 'title'), :action => '/books'
end

# User List Page
get '/users' do
	haml :user, {}, :users => User.all(:order => 'name'), :action => '/users'
end


# Add Book
post '/books' do
	Book.create(params[:post])
	redirect '/books'
end

# Add Comment
post '/comments' do
	params[:post][:created_date] = Time.now

	b = Book.find(params[:post][:book_id])
	b.comments << Comment.new(:content => params[:post][:content], :created_date => Time.now, :user_id => params[:post][:user_id])
	b.save

	redirect '/'
end

# Add User
post '/users' do
	User.create(params[:post])
	redirect '/users'
end
