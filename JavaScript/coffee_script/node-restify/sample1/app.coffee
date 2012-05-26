
restify = require 'restify'

server = restify.createServer()

server.use restify.queryParser()
server.use restify.bodyParser()

server.get '/sample', (req, res, next) ->
	res.send req.params
	next()

server.post '/sample2', (req, res, next) ->
	console.log req.params

	json = 
		res: '0'
		name: 'test'

	res.send json
	next()

server.listen 8080, -> console.log "listen : #{server.name}, #{server.url}"

