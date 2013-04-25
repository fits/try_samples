require 'net/https'
require 'cgi'

url = URI.parse('https://datamarket.accesscontrol.windows.net/v2/OAuth2-13')
clientId = ARGV[0]
secretKey = ARGV[1]

https = Net::HTTP.new(url.host, url.port)
https.use_ssl = true
https.verify_mode = OpenSSL::SSL::VERIFY_NONE

puts URI.encode(ARGV[1])

parameter = "grant_type=client_credentials&client_id=#{CGI.escape(clientId)}&client_secret=#{CGI.escape(secretKey)}&scope=http://api.microsofttranslator.com"

#puts parameter

#https.set_debug_output $stderr

res = https.post(url.request_uri, parameter)

#p res

puts res.code
puts res.body
