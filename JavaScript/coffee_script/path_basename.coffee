
path = require 'path'

# 1.html
console.log path.basename('/test/1.html')

console.log '-----'
# 1.html?a=b&b=1
console.log path.basename('/test/1.html?a=b&b=1')
