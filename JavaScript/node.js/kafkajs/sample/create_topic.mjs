import { Kafka } from 'kafkajs'

const clientId = process.env.KAFKA_CLIENT_ID ?? 'admin1'
const broker = process.env.KAFKA_BROKER ?? 'localhost:9092'

const topic = process.argv[2]
const num = parseInt(process.argv[3] ?? "10")

const kafka = new Kafka({
    clientId,
    brokers: [ broker ]
})

const admin = kafka.admin()

const run = async () => {
    await admin.connect()

    try {
        const r = await admin.createTopics({ topics: [ { topic, numPartitions: num } ] })

        console.log(`create topic: ${r}`)

    } finally {
        await admin.disconnect()
    }
}

run().catch(err => console.error(err))
