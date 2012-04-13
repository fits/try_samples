
https = require 'https'
url = require 'url'

getNextPath = (linkHeader) -> 
	m = linkHeader?.match /<([^>]*)>; rel="next"/
	url.parse(m[1]).path if m?[1]

getData = (path, callback) ->
	options =
		host: 'api.github.com'
		path: path

	req = https.get options, (res) ->
		buf = ''
		res.on 'data', (d) -> buf += d
		res.on 'end', ->
			callback watch for watch in JSON.parse(buf.toString())
			nextPath = getNextPath(res.headers['link'])
			getData nextPath, callback if nextPath?

	req.on 'error', (e) -> 
		console.error e
		callback null

getUsersWatches = (user, callback) ->
	getData "/users/#{user}/watched?per_page=100", callback


user = process.argv[2]

getUsersWatches user, (res) ->
	console.log "#{res.id}, #{res.name}, #{res.url}"

