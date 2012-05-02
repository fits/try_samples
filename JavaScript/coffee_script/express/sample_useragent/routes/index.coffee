
viewutils = require './viewutils'

exports.index = (req, res) ->

	page = viewutils.getView 'index', req

	console.log "referer : #{header req, 'referer'}"

	console.log "ip : #{remoteAddress req}"

	console.log header(req, 'user-agent')

	res.render page, { title: header(req, 'user-agent') }


remoteAddress = (req) ->
	if req.headers['x-forwarded-for']?
		req.headers['x-forwarded-for'].split(',')[0]
	else
		req.connection.remoteAddress

header = (req, key) ->
	if req.headers[key]?
		req.headers[key]
	else
		''
