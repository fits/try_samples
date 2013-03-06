system = require 'system'

if system.args.length < 3
	console.log "phantomjs #{system.args[0]} <url> <dest file>"
	phantom.exit()

page = require('webpage').create()

page.open system.args[1], (status) ->
	if status is 'success'
		# ページの内容を画像ファイルへ出力
		page.render system.args[2]

	phantom.exit()
