
fs = require 'fs'
markdown = require('markdown').markdown

fs.readFile process.argv[2], (err, data) ->
	console.log markdown.toHTML data.toString()
