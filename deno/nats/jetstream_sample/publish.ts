
import { connect, JSONCodec } from 'nats'

const message = Deno.args[0]

type Message = { id: number, message: string, date: Date }

const nc = await connect()

const jsm = await nc.jetstreamManager()
await jsm.streams.add({ name: 'js-sample1', subjects: ['js-sample1.*'] })

const js = nc.jetstream()

const sc = JSONCodec<Message>()

const msg = { id: Date.now(), message, date: new Date() }

const pa = await js.publish('js-sample1.step1', sc.encode(msg))

console.log(`published: ${JSON.stringify(pa)}`)

await nc.close()
