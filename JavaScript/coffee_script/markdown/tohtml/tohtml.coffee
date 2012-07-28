
fs = require 'fs'
markdown = require 'markdown'

fs.readFile process.argv[2], (err, data) ->
	console.log markdown.markdown.toHTML data.toString()
