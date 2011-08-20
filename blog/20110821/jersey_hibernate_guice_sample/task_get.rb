require "net/http"
require "uri"

puts Net::HTTP.get(URI.parse("http://localhost:8080/sample/task"))
