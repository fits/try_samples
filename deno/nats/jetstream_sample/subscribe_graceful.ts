
import { connect, consumerOpts, createInbox, JSONCodec } from 'nats'

const durableName = Deno.args[0]

type Message = { id: number, message: string, date: Date }

const nc = await connect()

const jsm = await nc.jetstreamManager()
await jsm.streams.add({ name: 'js-sample1', subjects: ['js-sample1.*'] })

const js = nc.jetstream()

const opts = consumerOpts()
opts.durable(durableName)
opts.manualAck()
opts.ackExplicit()
opts.deliverTo(createInbox())

const sub = await js.subscribe('js-sample1.*', opts)

const terminate = () => {
    sub.unsubscribe()
    console.log('*** unsubscribed')

    nc.close().finally(() => { console.log('*** closed') })
}

Deno.addSignalListener('SIGINT', terminate)
Deno.addSignalListener('SIGTERM', terminate)

const sc = JSONCodec<Message>()

for await (const m of sub) {
    const msg = sc.decode(m.data)
    console.log(`sid: ${m.sid}, seq: ${m.seq}, subject: ${m.subject}, data: ${JSON.stringify(msg)}`)

    const r = await m.ackAck()
    console.log(`**** ack: ${r}`)
}
