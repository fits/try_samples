
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
    console.log('*** connected')

    client.publish(publishTopic, publishMessage, { qos }, err => {
        if (err) {
            console.error(err)
        }
        else {
            console.log('*** published')
        }
    })

    client.end()
})

client.on('close', () => {
    console.log('*** closed')
})
