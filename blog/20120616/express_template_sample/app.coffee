
express = require 'express'

app = express()

app.configure ->
	app.set 'view engine', 'sample'

app.get '/', (req, res) ->
	res.render 'index', {title: 'test'}

app.listen 3000, -> console.log "server started"
