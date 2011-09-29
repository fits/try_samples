require "rubygems"
require "sinatra/base"
require "webrick"
require "webrick/https"
require "openssl"

class SampleApp < Sinatra::Base
	use Rack::Auth::Basic do |user, pass|
		user == 'user1'
	end

	get '/' do
		'hello'
	end

	post '/' do
		puts "post /"
		puts params
	end
end

Rack::Handler::WEBrick.run SampleApp, :Port => 8443, :SSLVerifyClient => OpenSSL::SSL::VERIFY_NONE, :SSLEnable => true, :SSLCertName => [ [ "CN",WEBrick::Utils::getservername ] ]
