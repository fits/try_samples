http = require 'http'
url = require 'url'
fs = require 'fs'
path = require 'path'

dir = process.argv[2]

printError = (urlString, error) ->
	console.log "failed: #{urlString}, #{error.message}"

process.stdin.resume()

`await {`
process.stdin.on 'data', defer(urls)
`}`

urls.toString().trim().split('\n').forEach (u) ->
	trgUrl = url.parse u

	`await {`
	http.get(trgUrl, defer(res)).on 'error', (err) -> printError u, err
	`}`

	res.setEncoding 'binary'
	buf = ''

	`await {`
	res.on 'data', (chunk) -> buf += chunk
	res.on 'end', defer()
	res.on 'close', (err) -> printError trgUrl.href, err if err
	`}`

	filePath = path.join dir, path.basename(trgUrl.pathname)

	`await {`
	fs.writeFile filePath, buf, 'binary', defer(err)
	`}`

	if err
		printError trgUrl.href, err
	else
		console.log "downloaded: #{trgUrl.href} => #{filePath}"
