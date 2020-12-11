
const mqtt = require('mqtt')

const uri = 'mqtt://localhost'

const qos = 1
const subscribeTopic = process.argv[2]
const shareName = process.argv[3]

const subscribeFilter = `$share/${shareName}/${subscribeTopic}`

const client = mqtt.connect(uri, { protocolVersion: 5 })

const end = () => {
    client.unsubscribe(subscribeFilter)
    client.end()
}

process.on('SIGINT', signal => end())
process.on('SIGTERM', signal => end())

client.on('error', err => {
    console.error(err)
    end()
})

client.on('connect', connack => {
    console.log('*** connected')

    client.subscribe(subscribeFilter, { qos })
})

client.on('message', (topic, message, packet) => {
    console.log(`*** received: topic=${topic}, message=${message}`)
})

client.on('close', () => {
    console.log('*** closed')
})
