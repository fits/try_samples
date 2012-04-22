dgram = require 'dgram'

server = dgram.createSocket 'udp4'

server.on 'message', (msg, rinfo) ->
	console.log "#{msg}, #{rinfo.address} : #{rinfo.port}"

server.on 'listening', ->
	addr = server.address()
	console.log "address : #{addr.address} : #{addr.port}"

server.bind 41234

server.addMembership '224.0.0.2'
