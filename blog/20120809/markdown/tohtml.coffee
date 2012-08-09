mk = require 'markdown'

process.stdin.resume()

process.stdin.on 'data', (data) ->
	console.log mk.markdown.toHTML data.toString()
