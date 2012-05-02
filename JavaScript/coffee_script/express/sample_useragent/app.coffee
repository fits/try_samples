
express = require 'express'
routes = require './routes'
http = require 'http'
utils = require './utils'

app = express()

app.configure ->
	app.set 'views', __dirname + '/views'
	app.set 'view engine', 'ejs'
	app.use express.favicon()
	app.use express.logger('dev')
	app.use express.static(__dirname + '/public')
	app.use express.bodyParser()
	app.use express.methodOverride()
	app.use utils.checkUserAgent
	app.use app.router

app.configure 'development', ->
	app.use express.errorHandler()

app.get '/', routes.index

http.createServer(app).listen(3000)

console.log "Express server listening on port 3000"
