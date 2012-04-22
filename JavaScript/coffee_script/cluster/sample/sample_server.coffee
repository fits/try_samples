cluster = require 'cluster'

workersNum = 2

if cluster.isMaster
	cluster.fork() for i in [0...workersNum]

	cluster.on 'death', (worker) -> console.log "worker death : #{worker.pid}"

else
	net = require 'net'

	server = net.createServer (c) ->
		c.on 'data', (d) -> console.log "recv: #{d.toString()}"
		c.on 'end', -> console.log 'disconnected'

	server.listen 9000, -> console.log 'ready'
