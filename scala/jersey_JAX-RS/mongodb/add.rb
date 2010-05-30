require 'net/http'

Net::HTTP.version_1_2

Net::HTTP.start('localhost', 8082) {|http|
	p http.post('/customers', "{\"title\": \"#{ARGV[0]}\", \"value\": \"#{ARGV[1]}\"}", {'Content-Type' => 'application/json'})

}
