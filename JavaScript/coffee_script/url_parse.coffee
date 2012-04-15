
url = require 'url'

console.log url.parse('http://localhost:8080/test?a=b&b=1')

console.log '-----'
# 第2引数に true を指定すると query が JavaScript オブジェクトになる
console.log url.parse('http://localhost:8080/test?a=b&b=1', true)

