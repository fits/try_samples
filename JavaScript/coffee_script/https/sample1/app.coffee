
https = require 'https'
fs = require 'fs'

options =
	key: fs.readFileSync 'keys/server.key'
	cert: fs.readFileSync 'keys/server.crt'

server = https.createServer options, (req, res) ->
	res.writeHead 200
	res.end "connect successful "

server.listen 8443
