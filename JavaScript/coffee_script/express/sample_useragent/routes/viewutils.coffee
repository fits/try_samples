
sufMap =
	isMobile: '_mb'
	isSmartPhone: '_sp'


exports.getView = (page, req) ->
	for k, v of sufMap
		if req[k]
			page = "#{page}#{v}"
			break

	page
