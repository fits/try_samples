net = require 'net'

end = '\r\n'
user = process.argv[2]
group = process.argv[3]

client = net.connect 9000, ->
	console.log 'connected'

	client.write "#{group}#{end}"

	process.stdin.resume()

	process.stdin.on 'data', (d) ->
		data = d.toString().trim()

		if data is 'q'
			client.end()
		else
			client.write "#{user} : #{data}#{end}"

	process.stdin.on 'end', -> console.log 'end'

client.on 'data', (d) ->
	console.log "#{d.toString()}"

client.on 'end', ->
	console.log 'disconnected'
	process.exit()

