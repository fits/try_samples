require 'rubygems'
require 'sinatra'

configure do
	@@vcap = ENV['VCAP_SERVICES']
end

get '/' do
	"vcap services : " + @@vcap
end
