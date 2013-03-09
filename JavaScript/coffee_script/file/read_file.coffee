fs = require 'fs'

stream = fs.createReadStream(process.argv[2], {encoding: 'utf-8'})

buf = ''

stream.on 'error', (ex) -> console.error ex
stream.on 'close', -> console.log '*** close'
stream.on 'end', -> console.log buf

stream.on 'data', (data) ->
	buf += data
