import { DaprServer, DaprInvokerCallbackContent, HttpMethod } from '@dapr/dapr'

const pubsubName = process.env.PUBSUB_NAME ?? 'pubsub'

type Message = {
    topic: string,
    message: string
}

const run = async () => {
    const server = new DaprServer()
    const opts = { method: HttpMethod.POST }

    server.invoker.listen('publish', async (data: DaprInvokerCallbackContent) => {
        const params = JSON.parse(data.body ?? '{}') as Message

        const r = await server.client.pubsub.publish(pubsubName, params.topic, params.message)

        return r
    }, opts)

    await server.start()
}

run().catch(err => {
    console.error(err)
    process.exit(1)
})
