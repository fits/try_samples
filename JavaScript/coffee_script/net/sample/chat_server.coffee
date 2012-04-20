
net = require 'net'

list = []

server = net.createServer (c) -> 
	console.log 'connected'

	list.push c

	c.on 'data', (d) -> console.log "recv: #{d.toString()}"
	c.on 'end', ->
		console.log 'disconnected'
		list = list.slice list.indexOf(c), 1

	for ci in list
		do (ci) ->
			c.pipe ci
			ci.pipe c if ci isnt c

server.listen 9000, -> console.log 'ready'
