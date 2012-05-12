require "rubygems"
require "sinatra/base"
require "webrick"

class SampleApp < Sinatra::Base

	post '/data' do
		p params

		data = request.body.read
		puts data

		'hello'
	end
end

Rack::Handler::WEBrick.run SampleApp, :Port => 8080
