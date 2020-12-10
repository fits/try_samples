
const aedes = require('aedes')()

const httpServer = require('http').createServer()
const ws = require('websocket-stream')

const port = 8083

ws.createServer({ server: httpServer }, aedes.handle)

httpServer.listen(port, () => {
    console.log(`Start ws: port=${port}`)
})

aedes.on('clientError', (client, err) => {
    console.log(`ClientError: client_id=${client?.id}`)
    console.error(err)
})

aedes.on('client', client => {
    console.log(`Client Connect: client_id=${client?.id}`)
})

aedes.on('publish', (msg, client) => {
    console.log(`Publish: client_id=${client?.id}, message=${JSON.stringify(msg)}`)
})

aedes.on('subscribe', (subsc, client) => {
    console.log(`Subscribe: client_id=${client?.id}, subscribe=${JSON.stringify(subsc)}`)
})
