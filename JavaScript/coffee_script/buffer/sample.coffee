
buf1 = new Buffer 'test123'
buf2 = new Buffer 'abc'

buf3 = new Buffer(buf1.length + buf2.length)

console.log buf3.length

buf1.copy(buf3)
buf2.copy(buf3, buf1.length)

console.log buf3.toString()
