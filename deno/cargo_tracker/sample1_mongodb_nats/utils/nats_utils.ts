
import { connect, NatsConnection, JSONCodec } from 'nats'

export const connectNats = connect

type Message<E> = { id: string, event: E, date: Date }

export class EventBroker<E> {
    private con: NatsConnection
    private codec

    constructor(con: NatsConnection) {
        this.con = con
        this.codec = JSONCodec<Message<E>>()
    }

    publish(subject: string, event: E) {
        const msg = { id: crypto.randomUUID(), event, date: new Date() }
        this.con?.publish(subject, this.codec.encode(msg))
    }
}
