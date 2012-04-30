
buf1 = new Buffer 'test123'
buf2 = new Buffer 'abc'
buf3 = new Buffer 'abc2'

size = [buf1, buf2, buf3].reduce (a, b) ->
	console.log a
	console.log b

	a + b.length
, 0

console.log size

