
fs = require 'fs'
https = require 'https'

if process.argv.length < 3
	console.log "#{require('path').basename(process.argv[1])} <json file>"
	process.exit()

jsonFile = process.argv[2]

fs.readFile jsonFile, (err, data) ->
	if err?
		console.error err
	else
		console.log data

		# 多バイト文字を使用している場合、Buffer と String で
		# length の値が異なる点に注意
		console.log data.length
		console.log data.toString().length

		options = 
			host: 'localhost'
			port: 8443
			path: '/data'
			method: 'POST'
			headers:
				'content-type': 'application/json; charset=UTF-8'
				# 以下は不要だが指定する際は Buffer の length を指定する
				'content-length': data.length

		req = https.request options, (res) ->
			console.log res
			buf = ''

			res.on 'data', (d) -> buf += d
			res.on 'end', -> console.log buf

		req.write data
		req.end()

