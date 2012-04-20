
net = require 'net'

server = net.createServer (c) -> 
	console.log 'connected'
	c.on 'end', -> console.log 'disconnected'

	c.write 'test\r\n'
	c.pipe c

server.listen 9000, -> console.log 'ready'
