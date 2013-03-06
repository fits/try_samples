system = require 'system'

if system.args.length < 2
	console.log "phantomjs #{system.args[0]} <url>"
	phantom.exit()

page = require('webpage').create()

page.open system.args[1], (status) ->
	if status is 'success'
		body = page.evaluate -> document.body.textContent
		console.log "body: #{body}"

	phantom.exit()
