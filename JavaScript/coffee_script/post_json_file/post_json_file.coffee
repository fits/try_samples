
fs = require 'fs'
http = require 'http'

if process.argv.length < 3
	console.log "#{require('path').basename(process.argv[1])} <json file>"
	process.exit()

jsonFile = process.argv[2]

fs.readFile jsonFile, (err, data) ->
	if err?
		console.error err
	else
		console.log data

		options = 
			host: 'localhost'
			port: 8080
			path: '/data'
			method: 'POST'
			headers:
				'content-type': 'application/json; charset=UTF-8'
				'content-length': data.length

		req = http.request options, (res) ->
			console.log res
			buf = ''

			res.on 'data', (d) -> buf += d
			res.on 'end', -> console.log buf

		req.write data
		req.end()

