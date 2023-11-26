import { Kafka } from 'kafkajs'

const clientId = process.env.KAFKA_CLIENT_ID ?? 'admin1'
const broker = process.env.KAFKA_BROKER ?? 'localhost:9092'

const kafka = new Kafka({
    clientId,
    brokers: [ broker ]
})

const admin = kafka.admin()

const run = async () => {
    await admin.connect()

    try {
        const topics = await admin.listTopics()

        topics.forEach(t => console.log(`topic=${t}`))

        const gs = await admin.listGroups()

        gs.groups.forEach(g => console.log(`group: groupId=${g.groupId}, protocolType=${g.protocolType}`))

        const tm = await admin.fetchTopicMetadata()

        tm.topics.forEach(t => console.log(`topic metadata: name=${t.name}, partitions=${t.partitions.length}`))

    } finally {
        await admin.disconnect()
    }
}

run().catch(err => console.error(err))
