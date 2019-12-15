
const WebSocket = require('ws')

const topicName = process.argv[2]
const subscName = process.argv[3]

const topic = `ws://localhost:8080/ws/v2/consumer/persistent/public/default/${topicName}/${subscName}`

const ws = new WebSocket(topic)

ws.on('message', msg => {
    const recMsg = JSON.parse(msg)
    const msgBody = Buffer.from(recMsg.payload, 'base64').toString()

    console.log(recMsg)
    console.log(msgBody)

    ws.send(JSON.stringify({messageId: recMsg.messageId}))
})
