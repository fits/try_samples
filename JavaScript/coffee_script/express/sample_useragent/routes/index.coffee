
exports.index = (req, res) ->
	page = 'index'

	ua = req.headers['user-agent']

	if ua.match /DoCoMo|J-PHONE|Vodafone|SoftBank|UP.Browser|KDDI|WILLCOM|PDXGW|DDIPOCKET|emobile/
		page = "#{page}_mb"
	else if ua.match /iPhone|iPod|Android|BlackBerry|Windows Phone|Windows CE/
		page = "#{page}_sp"

	res.render page, { title: ua }

