
express = require 'express'
http = require 'http'

app = express()

app.configure ->
	app.set 'view engine', 'sample'

app.get '/', (req, res) ->
	res.render 'index', {title: 'test'}

http.createServer(app).listen(3000)

console.log "Express server listening on port 3000"
