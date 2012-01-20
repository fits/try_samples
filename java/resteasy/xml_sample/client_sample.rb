require "net/http"
require "uri"

url = URI.parse(ARGV[0])
data = ARGV[1]

http = Net::HTTP.new(url.host, url.port)

res = http.start do
	req = Net::HTTP::Post.new(url.path)
	req.body = data

	http.request(req)
end

p res
puts res.body
