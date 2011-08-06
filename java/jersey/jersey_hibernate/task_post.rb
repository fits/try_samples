require "net/http"
require "uri"

res = Net::HTTP.post_form(URI.parse("http://localhost:8080/jersey-hibernate-sample/task"), {
	"title" => "testdata"
})

puts res.body
