dgram = require 'dgram'

buf = new Buffer(process.argv[2])

client = dgram.createSocket 'udp4'

client.send buf, 0, buf.length, 41234, '224.0.0.2', (err, bytes) ->
	console.log "#{err}, #{bytes}"
	client.close()

