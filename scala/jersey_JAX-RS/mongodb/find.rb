require 'net/http'

Net::HTTP.version_1_2

Net::HTTP.start('localhost', 8082) {|http|
	puts http.get('/customers').body
	
	
}