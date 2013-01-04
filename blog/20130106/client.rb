#coding:utf-8
require 'net/http'
require 'json/pure'

Net::HTTP.start('localhost', 8080) { |http|
	# GET 処理
	res = http.get('/user/1')
	puts "#{res}, #{res.code}, #{res.content_type}, #{res.body}"

	data = {
		'name' => 'test',
		'note' => 'サンプル'
	}
	# POST 処理
	res = http.post('/user', JSON.generate(data), {'Content-Type' => 'application/json'})
	puts "#{res}, #{res.code}, #{res.body}"
}