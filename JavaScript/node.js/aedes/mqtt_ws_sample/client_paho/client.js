
const Paho = require('paho-mqtt')

globalThis.WebSocket = require('ws')

const clientId = ''

const host = 'localhost'
const port = 8083
const topic = process.argv[2]
const message = process.argv[3]

const connect = client => new Promise((resolve, reject) => {
    client.connect({
        onSuccess: () => resolve(client),
        onFailure: reject
    })
})

const run = async () => {
    const client = await connect(new Paho.Client(host, port, clientId))

    client.onMessageArrived = msg => {
        console.log(`received: ${JSON.stringify(msg)}`)

        client.end()
    }

    client.subscribe(topic, {
        onSuccess: res => console.log(`publish: ${JSON.stringify(res)}`),
        onFailure: err => console.error(err)
    })

    client.publish(topic, message)
}

run().catch(err => console.error(err))
