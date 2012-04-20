
net = require 'net'

client = net.connect 9000, ->
	console.log 'connected'
	client.write 'test1111\r\n'
	client.write 'abc\r\n'

client.on 'data', (d) ->
	console.log "recv: #{d.toString()}"
	client.end()

client.on 'end', -> console.log 'disconnected'

