
const WebSocket = require('ws')

const server = new WebSocket.Server({ port: 9000 })

server.on('listening', () => {
	console.log(`server start: ${server.options.port}`)
})

server.on('connection', (ws, req) => {
	console.log('*** connect client')

	ws.on('message', msg => {
		console.log(msg)
		ws.send(msg)
	})

	ws.on('close', () => console.log('*** close client'))
})
