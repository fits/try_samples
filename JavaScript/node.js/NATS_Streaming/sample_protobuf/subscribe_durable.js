
const sample = require('./event_pb.js')

const clusterId = 'test-cluster'
const clientId = 'c1'
const subject = 'sample-protobuf'

const type = 'binary'

const durableName = process.argv[2]

const stan = require('node-nats-streaming').connect(clusterId, clientId)

stan.on('connect', () => {
    const opts = stan.subscriptionOptions()
                        .setDeliverAllAvailable()
                        .setDurableName(durableName)

    const subsc = stan.subscribe(subject, opts)

    subsc.on('message', msg => {
        const dataStr = msg.getData()
        const buf = Buffer.from(dataStr, type)

        const data = sample.DataEvent.deserializeBinary(buf)

        console.log(`${msg.getSequence()}, ${JSON.stringify(data.toObject())}`)
    })
})

stan.on('close', () => {
    process.exit()
})

process.stdin.on('data', d => {
    stan.close()
})
