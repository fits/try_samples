
import { connect, consumerOpts, createInbox, JSONCodec } from 'nats'

import { CargoEvent } from '../cargo/cargo.ts'
import { Message } from '../utils/nats_utils.ts'
import { postQuery } from '../utils/graphql_utils.ts'

const msgServer = Deno.env.get('MESSAGING_SERVER') ?? 'localhost'
const durableName = Deno.env.get('DURABLE_NAME') ?? 'cargo-handler1'

const deliveryEndpoint = Deno.env.get('DELIVERY_ENDPOINT') ?? 'http://localhost:8082'

const createDelivery = (tid: string) => postQuery(
    deliveryEndpoint, 
    `
        mutation ($tid: ID!) {
            create(trackingId: $tid) {
                __typename
                trackingId
            }
        }
    `,
    { tid }
)

const nc = await connect({ servers: msgServer })

const jsm = await nc.jetstreamManager()
await jsm.streams.add({ name: 'cargo-event', subjects: ['cargo-event.*'] })

const js = nc.jetstream()

const opts = consumerOpts()
opts.durable(durableName)
opts.manualAck()
opts.ackExplicit()
opts.deliverTo(createInbox())

const sub = await js.subscribe('cargo-event.*', opts)

const terminate = () => {
    sub.unsubscribe()
    nc.close()
}

Deno.addSignalListener('SIGINT', terminate)
Deno.addSignalListener('SIGTERM', terminate)

const codec = JSONCodec<Message<CargoEvent>>()

for await (const m of sub) {
    const msg = codec.decode(m.data)

    if (msg.event.tag == 'cargo-event.created') {
        const tracingId = msg.event.trackingId

        const r = await createDelivery(tracingId)

        if (r.data?.create) {
            console.log(JSON.stringify(r))
        }
        else {
            console.error(`[ERROR] failed create delivery: tracingId = ${tracingId}, result = ${JSON.stringify(r)}`)
        }
    }

    m.ack()
}
