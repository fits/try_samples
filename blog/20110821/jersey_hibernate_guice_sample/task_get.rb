require "net/http"

puts Net::HTTP.get(URI.parse("http://localhost:8080/sample/task"))
