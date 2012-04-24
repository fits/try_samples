
viewutils = require './viewutils'

exports.index = (req, res) ->

	page = viewutils.getView 'index', req

	res.render page, { title: req.headers['user-agent'] }

