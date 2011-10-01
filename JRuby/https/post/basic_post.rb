require 'net/https'
require 'uri'

url = URI.parse(ARGV[0])
user = ARGV[1]
pass = ARGV[2]
postData = ARGV[3]

https = Net::HTTP.new(url.host, url.port)
https.use_ssl = true
https.verify_mode = OpenSSL::SSL::VERIFY_NONE

res = https.start do
	req = Net::HTTP::Post.new(url.path)
	req.basic_auth user, pass

	req.body = postData

	https.request(req)
end

puts res.body
