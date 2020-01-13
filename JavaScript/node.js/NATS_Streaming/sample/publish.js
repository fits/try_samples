
const clusterId = 'test-cluster'
const clientId = process.argv[2]
const subject = process.argv[3]
const msg = process.argv[4]

const stan = require('node-nats-streaming').connect(clusterId, clientId)

stan.on('connect', () => {
    stan.publish(subject, msg, (err, guid) => {
        if (err) {
            console.error(err)
        }
        else {
            console.log(guid)
        }

        stan.close()
    })
})
