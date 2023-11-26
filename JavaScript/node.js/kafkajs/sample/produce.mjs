import { Kafka, Partitioners } from 'kafkajs'

const clientId = process.env.KAFKA_CLIENT_ID ?? 'pub1'
const broker = process.env.KAFKA_BROKER ?? 'localhost:9092'

const topic = process.argv[2] ?? 'sample'
const msg = process.argv[3] ?? 'test message'

const kafka = new Kafka({
    clientId,
    brokers: [ broker ]
})

const producer = kafka.producer({
    createPartitioner: Partitioners.DefaultPartitioner
})

const run = async () => {
    await producer.connect()

    try {
        await producer.send({
            topic,
            messages: [
                { value: msg }
            ]
        })
    } finally {
        await producer.disconnect()
    }
}

run().catch(err => console.error(err))
