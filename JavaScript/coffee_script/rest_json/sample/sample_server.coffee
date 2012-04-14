
http = require 'http'

http.createServer((req, res) ->
	console.log "request to : #{req.url}"

	data = [
		{id: 'test1', name: 'aaaaa'}
		{id: 'test2', name: 'bbb'}
	]

	jsonData = JSON.stringify data

	res.writeHead 200, 
		'Content-Length': jsonData.length
		'Content-Type': 'application/json'

	res.end jsonData

).listen 8081
