
const WebSocket = require('ws')

const topicName = process.argv[2]
const msgBody = process.argv[3]

const topic = `ws://localhost:8080/ws/v2/producer/persistent/public/default/${topicName}`

const ws = new WebSocket(topic)

const message = {
    payload: Buffer.from(msgBody).toString('base64'),
    properties: {
        category: 'test'
    },
    context: '1'
}

ws.on('open', () => {
    ws.send(JSON.stringify(message))
})

ws.on('message', m => {
    console.log(m)

    ws.terminate()
})
