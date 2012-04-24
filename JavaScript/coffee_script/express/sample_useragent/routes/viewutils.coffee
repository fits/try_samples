
uaMap =
	_mb: [
		'DoCoMo'
		'UP.Browser'
		'J-PHONE'
		'Vodafone'
		'SoftBank'
		'KDDI'
		'WILLCOM'
		'PDXGW'
		'DDIPOCKET'
		'emobile'
	]
	_sp: [
		'iPhone'
		'iPod'
		'Android'
		'Windows Phone'
		'Windows CE'
		'BlackBerry'
	]

exports.getView = (page, req) ->
	ua = req.headers['user-agent']

	for k, v of uaMap
		page = "#{page}#{k}" if ua.match new RegExp(v.join '|')

	page
