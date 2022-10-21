
import { connect, JSONCodec } from 'https://deno.land/x/nats@v1.8.0/src/mod.ts'

const message = Deno.args[0]

type Message = { id: number, message: string, date: Date }

const nc = await connect()

console.log(nc.info)

const sc = JSONCodec<Message>()

nc.publish('sample', sc.encode({ id: Date.now(), message, date: new Date() }))

console.log(`publish: ${message}`)

await nc.flush()
await nc.close()
