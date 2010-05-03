require 'jcode'
require 'net/http'

$KCODE='u'

Net::HTTP.version_1_2

boundary='--AaB03x'

CRLF = "\r\n"

data = "--#{boundary}#{CRLF}"
data += "content-disposition: form-data; name=\"username\"#{CRLF}#{CRLF}"
data += "bbb"

data += "#{CRLF}--#{boundary}#{CRLF}"
data += "content-disposition: form-data; name=\"commit\"#{CRLF}#{CRLF}"
data += "1"

data += "#{CRLF}--#{boundary}#{CRLF}"
data += "content-disposition: form-data; name=\"save[userfile]\"; filename=\"test.log\"#{CRLF}"
data += "Content-Type: application/octet-stream#{CRLF}#{CRLF}"
data += "てすと"

data += "#{CRLF}--#{boundary}--#{CRLF}"


Net::HTTP.start('localhost', 3000) {|http|

	puts data

	res = http.post('/upload/regist', data, {'Content-Type' => "multipart/form-data; boundary=#{boundary}"})

	puts res
}
