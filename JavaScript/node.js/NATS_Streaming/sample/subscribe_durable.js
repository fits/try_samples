
const clusterId = 'test-cluster'
const clientId = process.argv[2]
const subject = process.argv[3]
const durableName = process.argv[4]

const stan = require('node-nats-streaming').connect(clusterId, clientId)

stan.on('connect', () => {
    const opts = stan.subscriptionOptions()
                        .setDeliverAllAvailable()
                        .setDurableName(durableName)

    const subsc = stan.subscribe(subject, opts)

    subsc.on('message', msg => {
        console.log(`${msg.getSequence()}, ${msg.getData()}`)
    })
})

stan.on('close', () => {
    process.exit()
})

process.stdin.on('data', d => {
    stan.close()
})
