
uaMap =
	isMobile: [
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
	isSmartPhone: [
		'iPhone'
		'iPod'
		'Android'
		'Windows Phone'
		'Windows CE'
		'BlackBerry'
	]

exports.checkUserAgent = (req, res, next) ->
	ua = req.headers['user-agent']

	for k, v of uaMap
		if ua.match new RegExp(v.join '|')
			req[k] = true
			break

	next()
