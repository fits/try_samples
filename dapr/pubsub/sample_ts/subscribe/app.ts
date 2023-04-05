import { DaprServer } from '@dapr/dapr'

const pubsubName = process.env.PUBSUB_NAME ?? 'pubsub'
const topic = process.env.TOPIC ?? 'sample'

const run = async () => {
    const server = new DaprServer()

    server.pubsub.subscribe(pubsubName, topic, async (data, headers) => {
        console.log(`received: data=${JSON.stringify(data)}, headers=${JSON.stringify(headers)}`)
    })

    await server.start()
}

run().catch(err => {
    console.error(err)
    process.exit(1)
})
