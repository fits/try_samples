require 'net/https'
require 'cgi'
require 'json'

url = URI.parse('https://datamarket.accesscontrol.windows.net/v2/OAuth2-13')
clientId = ARGV[0]
secretKey = ARGV[1]
message = ARGV[2]

https = Net::HTTP.new(url.host, url.port)
https.use_ssl = true
https.verify_mode = OpenSSL::SSL::VERIFY_NONE

puts URI.encode(ARGV[1])

parameter = "grant_type=client_credentials&client_id=#{CGI.escape(clientId)}&client_secret=#{CGI.escape(secretKey)}&scope=http://api.microsofttranslator.com"

res = https.post(url.request_uri, parameter)

token = JSON.parse res.body

translatorUrl = "http://api.microsofttranslator.com/V2/Ajax.svc/Translate?appId=Bearer%20#{CGI.escape(token['access_token'])}&to=ja&text=#{CGI.escape(message)}"

Net::HTTP.get_print URI.parse(translatorUrl)
