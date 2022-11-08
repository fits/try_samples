
import { connect, NatsConnection, JSONCodec, JetStreamClient } from 'nats'

export const connectNats = connect

export type Message<E> = { id: string, event: E, date: Date }

export class StreamEventBroker<E> {
    private js: JetStreamClient
    private codec

    constructor(con: NatsConnection) {
        this.js = con.jetstream()
        this.codec = JSONCodec<Message<E>>()
    }

    async publish(subject: string, event: E) {
        const msg = { id: crypto.randomUUID(), event, date: new Date() }
        await this.js.publish(subject, this.codec.encode(msg))
    }
}
