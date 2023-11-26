import { Kafka } from 'kafkajs'

const clientId = process.env.KAFKA_CLIENT_ID ?? 'sub1'
const broker = process.env.KAFKA_BROKER ?? 'localhost:9092'

const topic = process.argv[2] ?? 'sample'
const groupId = process.argv[3] ?? 'group1'

const kafka = new Kafka({
    clientId,
    brokers: [ broker ]
})

const consumer = kafka.consumer({ groupId })

const run = async () => {
    await consumer.connect()

    await consumer.subscribe({ topic })

    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            const msg = message.value.toString()

            console.log(`received topic=${topic}, partition=${partition}, message=${msg}`)
        }
    })
}

run().catch(err => console.error(err))
