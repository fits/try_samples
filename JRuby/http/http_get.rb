require 'net/http'

url = URI.parse(ARGV[0])
host = ARGV[1]

http = Net::HTTP.new(url.host, url.port)

res = http.get(url.request_uri, 'Host' => host)

#p res

#puts res.code
puts res.body
