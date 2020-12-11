
const mqtt = require('mqtt')

const uri = 'mqtt://localhost'

const qos = 1
const publishTopic = process.argv[2]
const publishMessage = process.argv[3]

const client = mqtt.connect(uri, { protocolVersion: 5 })

client.on('error', err => {
    console.error(err)
    client.end()
})

client.on('connect', connack => {
    console.log(`*** connected: connack=${JSON.stringify(connack)}`)

    const responseTopic = `${publishTopic}/response/${client.options.clientId}`

    client.subscribe(responseTopic, { qos })

    client.publish(publishTopic, publishMessage, { qos, properties: { responseTopic } })
})

client.on('message', (topic, message, packet) => {
    console.log(`*** received: topic=${topic}, message=${message}, packet=${JSON.stringify(packet)}`)
    client.end()
})

client.on('close', () => {
    console.log('*** closed')
})
