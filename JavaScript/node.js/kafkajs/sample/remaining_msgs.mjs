import { Kafka } from 'kafkajs'

const clientId = process.env.KAFKA_CLIENT_ID ?? 'admin1'
const broker = process.env.KAFKA_BROKER ?? 'localhost:9092'

const topic = process.argv[2]
const groupId = process.argv[3]

const kafka = new Kafka({
    clientId,
    brokers: [ broker ]
})

const admin = kafka.admin()

const run = async () => {
    await admin.connect()

    try {
        const res = {}

        const ts = await admin.fetchTopicOffsets(topic)

        ts.forEach(t => {
            res[t.partition] = { current: parseInt(t.high) }
        })

        const os = await admin.fetchOffsets({
            groupId,
            topics: [ topic ]
        })

        os[0].partitions.forEach(p => {
            const tmp = res[p.partition]

            const offset = Math.max(0, p.offset)

            res[p.partition] = Object.assign(tmp, { consumed: offset, diff: tmp.current - offset })
        })

        console.log(res)

        const remain = Object.values(res).reduce((acc, v) => acc + v.diff, 0)

        console.log(`remaining messages: ${remain}`)

    } finally {
        await admin.disconnect()
    }
}

run().catch(err => console.error(err))
