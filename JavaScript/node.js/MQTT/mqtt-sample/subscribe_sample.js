
const mqtt = require('mqtt')
const client = mqtt.connect('mqtt://localhost')

const topic = process.argv[2]

client.on('connect', () => {
	client.subscribe(topic)
})

client.on('message', (topic, msg) => {
	console.log(`topic: ${topic}, msg: ${msg}`)
})
