
net = require 'net'

client = net.connect 9000, ->
	console.log 'connected'

	process.stdin.resume()

	process.stdin.on 'data', (d) ->
		if d.toString().trim() is 'q'
			client.end()
		else
			client.write d

	process.stdin.on 'end', -> console.log 'end'

client.on 'data', (d) ->
	console.log "recv: #{d.toString()}"

client.on 'end', ->
	console.log 'disconnected'
	process.exit()

