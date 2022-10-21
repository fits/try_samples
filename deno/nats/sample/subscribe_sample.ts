
import { connect, JSONCodec } from 'https://deno.land/x/nats@v1.8.0/src/mod.ts'

type Message = { id: number, message: string, date: Date }

const nc = await connect()

const sc = JSONCodec<Message>()

const sub = nc.subscribe('sample')

for await (const m of sub) {
    const msg = sc.decode(m.data)
    console.log(msg)
}
