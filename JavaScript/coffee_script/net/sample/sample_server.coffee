
net = require 'net'

server = net.createServer (c) -> 
	console.log 'connected'

	c.on 'data', (d) -> console.log "recv: #{d.toString()}"
	c.on 'end', -> console.log 'disconnected'

	c.write 'test\r\n'
	c.write '678\r\n'

server.listen 9000, -> console.log 'ready'
