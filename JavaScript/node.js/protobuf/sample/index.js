
const sample = require('./event_pb.js')

const ev1 = new sample.DataEvent()
ev1.setEventId('event123')

const d1 = new sample.Updated()
d1.setDataId('s1')
d1.setValue(-5)

ev1.setUpdated(d1)

console.log(ev1)
console.log(ev1.toObject())

const buf = ev1.serializeBinary()

const ev2 = sample.DataEvent.deserializeBinary(buf)

console.log(ev2)
console.log(ev2.toObject())
