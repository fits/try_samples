require 'net/https'
require 'uri'

url = URI.parse(ARGV[0])
user = ARGV[1]
pass = ARGV[2]
postData = ARGV[3]

res = Net::HTTP.start(url.host, url.port) do |https|
	https.use_ssl = true

	req = Net::HTTP::Post.new(url.path)
	req.basic_auth user, pass

	req.body = postData

	https.request(req)
end

puts res
