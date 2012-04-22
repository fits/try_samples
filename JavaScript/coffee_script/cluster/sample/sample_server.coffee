cluster = require 'cluster'

workersNum = 4

if cluster.isMaster
	cluster.fork() for i in [0...workersNum]

	cluster.on 'death', (worker) -> console.log "worker death : #{worker.pid}"

else if cluster.isWorker
	net = require 'net'

	server = net.createServer (c) ->
		c.on 'data', (d) -> console.log "recv #{process.pid}: #{d.toString()}"
		c.on 'end', -> console.log "disconnected #{process.pid}"

	server.listen 9000, -> console.log "ready #{process.pid}"
