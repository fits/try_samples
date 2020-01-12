
const nc = require('nats').connect()

const subject = process.argv[2]
const message = process.argv[3]

nc.publish(subject, message)

nc.flush(() => {
    nc.close()
})
