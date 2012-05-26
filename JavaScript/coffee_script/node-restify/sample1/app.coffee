
restify = require 'restify'

server = restify.createServer()

server.use restify.queryParser()
server.use restify.bodyParser()

server.get '/sample', (req, res, next) ->
	res.send req.params
	next()

server.post '/sample2', (req, res, next) ->
	console.log req.params

	unless req.params.user?
		next(new restify.InvalidArgumentError(req.params))
	else
		json = 
			res: '0'
			name: 'test'

		res.json json
		next()

server.listen 8080, -> console.log "listen : #{server.name}, #{server.url}"

