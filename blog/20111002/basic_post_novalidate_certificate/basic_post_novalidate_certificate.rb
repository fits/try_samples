require 'net/https'
require 'uri'

url = URI.parse(ARGV[0])
user = ARGV[1]
pass = ARGV[2]
postData = ARGV[3]

https = Net::HTTP.new(url.host, url.port)
#SSL使用の有効化
https.use_ssl = true
#SSL証明書の検証をしないための設定
https.verify_mode = OpenSSL::SSL::VERIFY_NONE

res = https.start do
	req = Net::HTTP::Post.new(url.path)
	#Basic認証
	req.basic_auth user, pass

	#POSTデータの設定
	req.body = postData

	#POST
	https.request(req)
end

#結果の出力
print res.body
