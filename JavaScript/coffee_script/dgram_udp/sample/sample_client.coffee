dgram = require 'dgram'

msg = new Buffer('test message')

client = dgram.createSocket 'udp4'

client.send msg, 0, msg.length, 41234, 'localhost', (err, bytes) ->
	console.log "#{err}, #{bytes}"
	client.close()

