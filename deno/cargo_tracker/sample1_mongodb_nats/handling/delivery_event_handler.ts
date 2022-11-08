
import { connect, consumerOpts, createInbox, JSONCodec } from 'nats'

import { HandlingEvent, TrackingId } from '../delivery/delivery.ts'
import { Message } from '../utils/nats_utils.ts'
import { postQuery } from '../utils/graphql_utils.ts'

const msgServer = Deno.env.get('MESSAGING_SERVER') ?? 'localhost'
const durableName = Deno.env.get('DURABLE_NAME') ?? 'delivery-handler1'

const deliveryEndpoint = Deno.env.get('DELIVERY_ENDPOINT') ?? 'http://localhost:8082'

const isMisdirected = async (tid: string) => {
    const r = await postQuery(
        deliveryEndpoint, 
        `
            query ($tid: ID!) {
                isMisdirected(trackingId: $tid)
            }
        `,
        { tid }
    )

    return r.data.isMisdirected
}

const isUnloadedAtDestination = async (tid: string) => {
    const r = await postQuery(
        deliveryEndpoint, 
        `
            query ($tid: ID!) {
                isUnloadedAtDestination(trackingId: $tid)
            }
        `,
        { tid }
    )

    return r.data.isUnloadedAtDestination
}

export interface Misdirected {
    tag: 'tracking-event.misdirected'
    trackingId: TrackingId
}

export interface UnloadedAtDestination {
    tag: 'tracking-event.unloaded-at-destination'
    trackingId: TrackingId
}

export type TrackingEvent = Misdirected | UnloadedAtDestination

const nc = await connect({ servers: msgServer })

const jsm = await nc.jetstreamManager()

await jsm.streams.add({ name: 'transport-event', subjects: ['transport-event.*'] })
await jsm.streams.add({ name: 'tracking-event', subjects: ['tracking-event.*'] })

const js = nc.jetstream()

const opts = consumerOpts()
opts.durable(durableName)
opts.manualAck()
opts.ackExplicit()
opts.deliverTo(createInbox())

const sub = await js.subscribe('transport-event.*', opts)

const terminate = () => {
    sub.unsubscribe()
    nc.close()
}

Deno.addSignalListener('SIGINT', terminate)
Deno.addSignalListener('SIGTERM', terminate)

const codec = JSONCodec<Message<HandlingEvent>>()
const trkCodec = JSONCodec<Message<TrackingEvent>>()

const publishEvent = (event: TrackingEvent) => js.publish(
    event.tag, 
    trkCodec.encode({
        id: crypto.randomUUID(),
        event,
        date: new Date()
    })
)

for await (const m of sub) {
    const msg = codec.decode(m.data)
    const trackingId = msg.event.trackingId

    const miss = await isMisdirected(trackingId)

    if (miss) {
        const r = await publishEvent({ tag: 'tracking-event.misdirected', trackingId })

        console.log(`[INFO] publish misdirected: trackingId = ${trackingId}, result = ${JSON.stringify(r)}`)
    }

    if (msg.event.tag == 'transport-event.unloaded') {
        const arrived = await isUnloadedAtDestination(trackingId)

        if (arrived) {
            const r = await publishEvent({ tag: 'tracking-event.unloaded-at-destination', trackingId })
            
            console.log(`[INFO] publish arrived: trackingId = ${trackingId}, result = ${JSON.stringify(r)}`)    
        }
    }

    m.ack()
}
