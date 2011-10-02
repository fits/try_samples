require "rubygems"
require "sinatra/base"
require "webrick/https"
require "openssl"

class SampleApp < Sinatra::Base
	#Basic認証
	use Rack::Auth::Basic do |user, pass|
		user == 'user1'
	end

	post '/' do
		p params
		'hello'
	end
end

#WEBrick で SSL を使用するための処理
#（実行時に自己証明書が自動生成される）
Rack::Handler::WEBrick.run SampleApp, {
	:Port => 8443, 
	:SSLEnable => true, 
	#クライアントを検証しないための設定
	:SSLVerifyClient => OpenSSL::SSL::VERIFY_NONE, 
	:SSLCertName => [
		["CN", WEBrick::Utils::getservername]
	]
}
