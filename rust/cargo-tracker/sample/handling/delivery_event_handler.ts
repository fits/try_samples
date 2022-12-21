
import { connect, consumerOpts, createInbox, JSONCodec } from 'nats'

const cargoEndpoint = Deno.env.get('CARGO_ENDPOINT') ?? 'http://localhost:8080'

const natsServer = Deno.env.get('NATS_SERVER') ?? 'localhost'
const durableName = Deno.env.get('DURABLE_NAME') ?? 'delivery-handler1'

const eventName = 'delivery-event'
const subject = `${eventName}.*`

const nc = await connect({ servers: natsServer })

const jsm = await nc.jetstreamManager()
await jsm.streams.add({ name: eventName, subjects: [subject] })

const js = nc.jetstream()

const opts = consumerOpts()
opts.durable(durableName)
opts.manualAck()
opts.ackExplicit()
opts.deliverTo(createInbox())

const sub = await js.subscribe(subject, opts)

const terminate = () => {
    sub.unsubscribe()
    nc.close()
}

Deno.addSignalListener('SIGINT', terminate)
Deno.addSignalListener('SIGTERM', terminate)

type DeliveryEvent = {
    [key: string]: {
        tracking_id: string,
        voyage_no?: string,
        location?: string,
        completion_time?: string,
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

const isDestination = (tid: string, loc: string) => postQuery(
    cargoEndpoint, 
    `
        query ($tid: ID!, $loc: ID!) {
            isDestination(trackingId: $tid, location: $loc)
        }
    `,
    { tid, loc }
)

const isOnRoute = (tid: string, loc: string, vno?: string) => postQuery(
    cargoEndpoint, 
    `
        query ($tid: ID!, $loc: ID!, $vno: ID) {
            isOnRoute(trackingId: $tid, location: $loc, voyageNo: $vno)
        }
    `,
    { tid, loc, vno }
)

const handleReceived = async (event: DeliveryEvent) => {
    const tid = event.Received?.tracking_id
    const loc = event.Received?.location!

    const r = await isOnRoute(tid, loc)

    if (r.data?.isOnRoute) {
        console.log(`received: trackingId=${tid}`)
    }
    else {
        console.log(`[WARN] misdirect received: trackingId=${tid}, location=${loc}`)
    }
}

const handleLoaded = async (event: DeliveryEvent) => {
    const tid = event.Loaded?.tracking_id
    const loc = event.Loaded?.location!
    const vno = event.Loaded?.voyage_no!

    const r = await isOnRoute(tid, loc, vno)

    if (r.data?.isOnRoute) {
        console.log(`loaded: trackingId=${tid}`)
    }
    else {
        console.log(`[WARN] misdirect loaded: trackingId=${tid}, voyageNo=${vno}, location=${loc}`)
    }
}

const handleUnloaded = async (event: DeliveryEvent) => {
    const tid = event.Unloaded?.tracking_id
    const loc = event.Unloaded?.location!

    const d = await isDestination(tid, loc)

    if (d.data?.isDestination) {
        console.log(`arrived: trackingId=${tid}, location=${loc}`)
    }
    else {
        const r = await isOnRoute(tid, loc)

        if (r.data?.isOnRoute) {
            console.log(`unloaded: trackingId=${tid}`)
        }
        else {
            console.log(`[WARN] misdirect unloaded: trackingId=${tid}, location=${loc}`)
        }    
    }
}

const codec = JSONCodec<DeliveryEvent>()

for await (const m of sub) {
    const msg = codec.decode(m.data)

    console.log('-----')
    console.log(`subject = ${m.subject}`)
    console.log(msg)

    switch (m.subject) {
        case `${eventName}.received`:
            await handleReceived(msg)
            break
        case `${eventName}.loaded`:
            await handleLoaded(msg)
            break
        case `${eventName}.unloaded`:
            await handleUnloaded(msg)
            break
    }

    m.ack()
}
