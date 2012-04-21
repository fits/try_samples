
net = require 'net'

list = []

server = net.createServer (c) -> 
	console.log 'connected'

	list.push c

	c.on 'data', (d) -> console.log "recv: #{d.toString()}"
	c.on 'end', ->
		console.log 'disconnected'
		list = (ci for ci in list when ci isnt c)

	for ci in list
		c.pipe ci
		ci.pipe c if ci isnt c

server.listen 9000, -> console.log 'ready'
