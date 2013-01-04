
restify = require 'restify'

server = restify.createServer()

server.use restify.bodyParser()

server.get '/user/:id', (req, res, next) ->
	res.json
		id: req.params.id
		name: 'restify sample'

	next()

server.post '/user', (req, res, next) ->
	data = JSON.parse req.body
	console.log data

	res.json null
	next()

server.listen 8080, -> console.log "server started ..."
