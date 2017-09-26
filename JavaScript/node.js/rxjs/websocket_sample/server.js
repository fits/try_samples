
const WebSocket = require('ws')
const Rx = require('rxjs')

const port = 9000

class WebSocketHandler {
	constructor(ws, observer) {
		this.ws = ws
		this.handler = null

		observer.next({action: 'add', value: this})

		ws.on('close', () => observer.next({action: 'remove', value: this}))

		ws.on('message', msg => {
			if (this.handler) {
				this.handler(msg)
			}
		})
	}

	send(msg) {
		this.ws.send(msg)
	}

	onMessage(handler) {
		this.handler = handler
	}
}

const wsSrc = Rx.Observable.create(observer => {
	const server = new WebSocket.Server({ port: port })

	server.on('listening', () => {
		console.log(`server start: ${server.options.port}`)
	})

	server.on('connection', (ws, req) => new WebSocketHandler(ws, observer))
	server.on('error', err => observer.error(err))
})

const wsList = wsSrc.scan(
	(acc, d) => {
		if (d.action == 'add') {
			acc.push(d.value)
		}
		else {
			acc = acc.filter(ws => ws != d.value)
		}
		return acc
	},
	[]
)

wsList.flatMap(wss =>
	Rx.Observable.create(observer => 
		wss.forEach(ws => ws.onMessage(msg => observer.next({
			message: msg,
			list: wss
		})))
	)
).subscribe(res => {
	console.log(`message: ${res.message}`)
	res.list.forEach(ws => ws.send(res.message))
})
