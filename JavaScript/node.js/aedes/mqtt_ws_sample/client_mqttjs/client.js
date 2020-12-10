
const mqtt = require('mqtt')

const brokerUri = 'ws://localhost:8083'

const qos = 0
const topic = process.argv[2]
const message = process.argv[3]

const client = mqtt.connect(brokerUri)

client.on('error', err => {
    console.error(err)
    client.end()
})

client.on('connect', () => {
    console.log('connect')

    client.subscribe(topic, { qos })
    client.publish(topic, message)
})

client.on('message', (tpc, msg, packet) => {
    console.log(`received: topic=${tpc}, message=${msg}, packet=${JSON.stringify(packet)}`)

    client.end()
})

client.on('close', () => {
    console.log('close')
})
