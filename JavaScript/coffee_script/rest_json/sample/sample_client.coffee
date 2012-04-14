
http = require 'http'

get = (path, callback) ->
	options =
		host: 'localhost'
		port: 8081
		path: path

	req = http.get options, (res) ->
		buf = ''
		res.on 'data', (d) -> buf += d
		res.on 'end', ->
			callback JSON.parse(buf.toString())

	req.on 'error', (e) -> 
		console.error e
		callback null

getCustomers = (callback) ->
	get "/list", callback


getCustomers (res) -> console.log(res)

