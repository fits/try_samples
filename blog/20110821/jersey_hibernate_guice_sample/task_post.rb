require "net/http"

res = Net::HTTP.post_form(URI.parse("http://localhost:8080/sample/task"), "title" => ARGV[0])

puts res.body
