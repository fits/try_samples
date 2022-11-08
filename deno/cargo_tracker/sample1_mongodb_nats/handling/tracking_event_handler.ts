
import { connect, consumerOpts, createInbox, JSONCodec } from 'nats'

import { Message } from '../utils/nats_utils.ts'

const msgServer = Deno.env.get('MESSAGING_SERVER') ?? 'localhost'
const durableName = Deno.env.get('DURABLE_NAME') ?? 'tracking-handler1'

const nc = await connect({ servers: msgServer })

const jsm = await nc.jetstreamManager()

await jsm.streams.add({ name: 'tracking-event', subjects: ['tracking-event.*'] })

const js = nc.jetstream()

const opts = consumerOpts()
opts.durable(durableName)
opts.manualAck()
opts.ackExplicit()
opts.deliverTo(createInbox())

const sub = await js.subscribe('tracking-event.*', opts)

const terminate = () => {
    sub.unsubscribe()
    nc.close()
}

Deno.addSignalListener('SIGINT', terminate)
Deno.addSignalListener('SIGTERM', terminate)

const codec = JSONCodec<Message<unknown>>()

for await (const m of sub) {
    const msg = codec.decode(m.data)

    console.log(`[INFO] tracking: ${JSON.stringify(msg)}`)

    m.ack()
}
