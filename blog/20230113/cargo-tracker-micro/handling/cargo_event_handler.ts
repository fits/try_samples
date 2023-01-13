
import { connect, consumerOpts, createInbox, JSONCodec } from 'nats'

const deliveryEndpoint = Deno.env.get('DELIVERY_ENDPOINT') ?? 'http://localhost:8081'

const natsServer = Deno.env.get('NATS_SERVER') ?? 'localhost'
const durableName = Deno.env.get('DURABLE_NAME') ?? 'cargo-handler1'

const nc = await connect({ servers: natsServer })

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

type CargoEvent = {
    [key: string]: {
        tracking_id: string,
        route_spec?: unknown,
        itinerary?: unknown,
        destination?: string,
        deadline?: string,
    }
}

const postQuery = async (url: string, query: string, variables?: unknown) => {
    const r = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ query, variables })
    })

    return r.json()
}

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

const handleCreated = async (event: CargoEvent) => {
    const tid = event.Created?.tracking_id

    if (tid) {
        const r = await createDelivery(tid)

        if (r.errors) {
            console.log(`ERROR create delivery: errors=${JSON.stringify(r.errors)}`)
        }
        else {
            console.log(`create delivery: result=${JSON.stringify(r.data)}`)
        }
    }
}

const codec = JSONCodec<CargoEvent>()

for await (const m of sub) {
    const msg = codec.decode(m.data)

    console.log('-----')
    console.log(`subject = ${m.subject}`)
    console.log(msg)

    if (m.subject == 'cargo-event.created') {
        await handleCreated(msg)
    }

    m.ack()
}
