
const grpc = require('@grpc/grpc-js')
const { GraphQLService } = require('./generated/proto/graphql_grpc_pb')
const { Struct } = require('google-protobuf/google/protobuf/struct_pb')

const { graphql, buildSchema, subscribe, parse } = require('graphql')

const { v4: uuidv4 } = require('uuid')

const schema = buildSchema(`
    enum Category {
        Standard
        Extra
    }

    input CreateItem {
        category: Category!
        value: Int!
    }

    type Item {
        id: ID!
        category: Category!
        value: Int!
    }

    type Mutation {
        create(input: CreateItem!): Item
    }

    type Query {
        find(id: ID!): Item
    }

    type Subscription {
        created: Item
    }
`)

class MessageBox {
    #promises = []
    #resolves = []

    #appendPromise = () => this.#promises.push(
        new Promise(res => this.#resolves.push(res))
    )

    publish(msg) {
        if (this.#resolves.length == 0) {
            this.#appendPromise()
        }

        this.#resolves.shift()(msg)
    }

    [Symbol.asyncIterator]() {
        return {
            next: async () => {
                console.log('*** asyncIterator next')

                if (this.#promises.length == 0) {
                    this.#appendPromise()
                }

                const value = await this.#promises.shift()
                return { value, done: false }
            }
        }
    }
}

class PubSub {
    #subscribes = []

    publish(msg) {
        this.#subscribes.forEach(s => s.publish(msg))
    }

    subscribe() {
        const sub = new MessageBox()
        this.#subscribes.push(sub)

        return sub
    }

    unsubscribe(sub) {
        this.#subscribes = this.#subscribes.filter(s => s != sub)
    }
}

const store = {}
const pubsub = new PubSub()

const root = {
    create: ({ input: { category, value } }) => {
        console.log(`*** call create: category = ${category}, value = ${value}`)

        const id = `item-${uuidv4()}`
        const item = { id, category, value }

        store[id] = item
        pubsub.publish({ created: item })

        return item
    },
    find: ({ id }) => {
        console.log(`*** call find: ${id}`)
        return store[id]
    }
}

const server = new grpc.Server()

server.addService(GraphQLService, {
   async query(call, callback) {
        try {
            const query = call.request.getQuery()
            const variables = call.request.getVariables().toJavaScript()

            const r = await graphql(schema, query, root, {}, variables)

            callback(null, Struct.fromJavaScript(r))

        } catch(e) {
            console.error(e)
            callback(e)
        }
    },
    async subscription(call) {
        console.log('*** subscribed')

        try {
            const query = call.request.getQuery()
            const variables = call.request.getVariables().toJavaScript()

            const sub = pubsub.subscribe()

            call.on('cancelled', () => {
                console.log('*** unsubscribed')
                pubsub.unsubscribe(sub)
            })

            const subRoot = {
                created: () => sub
            }

            const aiter = await subscribe(schema, parse(query), subRoot, {}, variables)

            for await (const r of aiter) {
                call.write(Struct.fromJavaScript(r))
            }
        } catch(e) {
            console.error(e)
            call.destroy(e)
        }
    }
})

server.bindAsync(
    '127.0.0.1:50051',
    grpc.ServerCredentials.createInsecure(),
    (err, port) => {
        if (err) {
            console.error(err)
            return
        }

        console.log(`start server: ${port}`)

        server.start()
    }
)
