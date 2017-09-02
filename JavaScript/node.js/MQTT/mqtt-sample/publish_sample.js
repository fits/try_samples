
const mqtt = require('mqtt')
const client = mqtt.connect('mqtt://localhost')

const topic = process.argv[2]
const msg = process.argv[3]

client.publish(topic, msg)

client.end()
