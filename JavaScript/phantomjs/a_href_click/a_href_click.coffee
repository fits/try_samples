
page = require('webpage').create()

page.open 'http://localhost/sample/', (status) ->
	page.onLoadFinished = (st) ->
		console.log page.title
		phantom.exit()

	if status is 'success'
		page.evaluate () ->
			ev = document.createEvent 'MouseEvents'
			ev.initMouseEvent('click', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)

			document.querySelector('div.sample a').dispatchEvent ev
	else
		phantom.exit()
