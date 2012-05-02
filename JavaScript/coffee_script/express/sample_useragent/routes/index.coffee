
viewutils = require './viewutils'

exports.index = (req, res) ->

	page = viewutils.getView 'index', req

	console.log "referer : #{req.headers['referer']}"

	if req.headers['x-forwarded-for']?
		console.log "ip : " + req.headers['x-forwarded-for'].split(',')[0]
	else
		console.log "ip : #{req.connection.remoteAddress}"

	res.render page, { title: req.headers['user-agent'] }

