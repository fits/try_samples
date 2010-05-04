require 'rubygems'
require 'sinatra'

get '/' do
	'test page'
end

get '/:name/:value' do |n, v|
	"test page : #{n} - #{v}"
end
