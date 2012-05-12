#coding:utf-8

require "rubygems"
require "sinatra/base"
require "webrick"

class SampleApp < Sinatra::Base

	post '/data' do
		p params

		# POST の内容を出力
		data = request.body.read
		puts data
		p data

		'hello'
	end
end

Rack::Handler::WEBrick.run SampleApp, :Port => 8080
