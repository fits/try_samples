#coding:utf-8

require 'net/http'
require 'uri'
require 'json/pure'

url = URI.parse('http://localhost:8080/sample2')

http = Net::HTTP.new(url.host, url.port)

param = {
	'user' => 'abc',
	'password' => 'pass'
}

data = JSON.generate param

puts data

res = http.start do
	req = Net::HTTP::Post.new(url.path)

	# Content-Type ‚Ìw’è‚Í•K{
	req.add_field 'Content-Type', "application/json"
	req.body = data

	http.request(req)
end

p res

puts res.body

