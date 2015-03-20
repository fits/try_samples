require 'net/http'

http = Net::HTTP.new('localhost', 8080)

res = http.post('/data', 'a=b')

p res

case res
	when Net::HTTPRedirection then
		res2 = http.get(res['Location'])
		puts res2.body
end
