dns = require 'native-dns'

server = dns.createServer()

server.on 'request', (req, res) ->
	console.log req

	# リクエストされたホスト名に関わらず 127.0.0.5 を返す
	res.answer.push dns.A
		name: req.question[0].name
		address: '127.0.0.5'
		ttl: 600

	res.send()

server.on 'error', (err, buf, req, res) -> console.log err.stack

server.serve 53
