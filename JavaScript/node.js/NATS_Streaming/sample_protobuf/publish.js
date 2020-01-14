
const sample = require('./event_pb.js')

const clusterId = 'test-cluster'
const clientId = 'p1'
const subject = 'sample-protobuf'

const eventId = process.argv[2]
const dataId = process.argv[3]

const data = new sample.DataEvent()
data.setEventId(eventId)

if (process.argv.length <= 4) {
    const crt = new sample.Created()
    crt.setDataId(dataId)

    data.setCreated(crt)
}
else {
    const upd = new sample.Updated()
    upd.setDataId(dataId)
    upd.setValue(parseInt(process.argv[4]))

    data.setUpdated(upd)
}

console.log(data.toObject())

const stan = require('node-nats-streaming').connect(clusterId, clientId)

stan.on('connect', () => {
    const msg = data.serializeBinary()

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
