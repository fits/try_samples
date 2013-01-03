
restify = require 'restify'

server = restify.createServer()

server.use restify.bodyParser()

server.get '/user/:id', (req, res, next) ->
	res.json
		id: req.params.id
		name: 'test'

	next()

server.post '/user', (req, res, next) ->
	data = JSON.parse req.body
	console.log data

	res.json {}
	next()

server.listen 8080, -> console.log "server started ..."
