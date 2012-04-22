dgram = require 'dgram'

msg = new Buffer('test message')

client = dgram.createSocket 'udp4'

client.send msg, 0, msg.length, 41234, '224.0.0.2', (err, bytes) ->
	console.log "#{err}, #{bytes}"
	client.close()

