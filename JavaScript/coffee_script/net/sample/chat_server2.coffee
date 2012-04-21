net = require 'net'

list = {}

server = net.createServer (c) -> 
	console.log 'connected'
	group = null

	c.on 'data', (d) ->
		if not group?
			group = d.toString().trim()

			console.log "select group : #{group}"

			list[group] = [] if not list[group]
			list[group].push c

			for ci in list[group]
				c.pipe ci
				ci.pipe c if ci isnt c

		else
			console.log "recv[#{group}] - #{d.toString()}"

	c.on 'end', ->
		console.log 'disconnected'

		if group?
			list[group] = (ci for ci in list[group] when ci isnt c)

server.listen 9000, -> console.log 'ready'
