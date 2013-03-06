system = require 'system'

if system.args.length < 2
	console.log "phantomjs #{system.args[0]} <host> <url>"
	phantom.exit()

page = require('webpage').create()

# ページのロード開始時のイベント処理
page.onLoadStarted = ->
	page.startTime = new Date()

# HTTPヘッダーの設定
page.customHeaders = 
	'Host': system.args[1]

page.open system.args[2], (status) ->
	if status is 'success'
		page.endTime = new Date()

		title = page.evaluate -> document.title
		console.log "time: #{page.endTime - page.startTime}, #{title}"
	else
		console.log 'failed'

	phantom.exit()
